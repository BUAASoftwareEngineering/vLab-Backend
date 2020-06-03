package org.nocturne.vslab.backend.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.bean.ImageType;
import org.nocturne.vslab.backend.docker.DockerClientFactory;
import org.nocturne.vslab.backend.docker.DockerHostConfig;
import org.nocturne.vslab.backend.service.ProjectService;
import org.nocturne.vslab.backend.util.CloudFileHelper;
import org.nocturne.vslab.backend.util.ContainerLiveKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DockerManagerImpl implements DockerManager {

    private static final Integer CONTAINER_SERVER_PORT = 3000;
    private static final Integer TERMINAL_PORT = 4000;
    private static final Integer LANGUAGE_PORT = 5000;

    private final ContainerLiveKeeper keeper;

    private final ProjectService projectService;

    private static final List<ExposedPort> exposedPortList = new ArrayList<>();
    private static final Ports portBindings;


    static {
        // set expose port
        exposedPortList.add(ExposedPort.tcp(CONTAINER_SERVER_PORT));
        exposedPortList.add(ExposedPort.tcp(TERMINAL_PORT));
        exposedPortList.add(ExposedPort.tcp(LANGUAGE_PORT));

        // bind expose port
        portBindings = new Ports();
        portBindings.bind(exposedPortList.get(0), Ports.Binding.empty());
        portBindings.bind(exposedPortList.get(1), Ports.Binding.empty());
        portBindings.bind(exposedPortList.get(2), Ports.Binding.empty());
    }

    @Lazy
    @Autowired
    public DockerManagerImpl(ContainerLiveKeeper keeper,
                             ProjectService projectService) {
        this.keeper = keeper;
        this.projectService = projectService;
    }

    /**
     * 该函数只会在数据库中预留一条记录，并不会实际操作docker创建容器
     */
    @Transactional
    @Override
    public Project createContainer(Integer userId, String projectName, ImageType imageType) {
        Project project = new Project(null, "null",
                imageType, projectName,
                "null", 0, 0, 0, true);
        projectService.createProject(userId, project);

        return project;
    }

    /**
     * 前置条件:
     * container 并未被创建 （db 中该 container 的 ip is null）
     *
     * 该函数会完成下列几项工作
     * 1. 随机选取 docker host，从云上下载文件然后创建并运行 container
     * 2. 更新数据库为实装数据
     * 3. 启动 keeper 监控容器活跃
     */
    @Transactional
    @Override
    public Boolean startContainer(Integer projectId) {
        Project project = projectService.getProjectById(projectId);
        if (!"null".equals(project.getIp())) return true;

        // create and run container on randomly chosen host
        String ip = DockerHostConfig.getIPRandomly();

        String containerId = createNewContainer(projectId, ip, project.getImageType());

        DockerClient dockerClient = DockerClientFactory.getDockerClient(ip);
        dockerClient.startContainerCmd(containerId).exec();

        // update container info to db
        Map<Integer, Integer> portMap = getBindingPortsOfContainer(ip, containerId);
        project.setServerPort(portMap.get(CONTAINER_SERVER_PORT));
        project.setTerminalPort(portMap.get(TERMINAL_PORT));
        project.setLanguagePort(portMap.get(LANGUAGE_PORT));

        project.setIp(ip);
        project.setContainerId(containerId);

        projectService.updateProject(project);

        // start keeper to monitor container alive
        keeper.refreshActiveTime(projectId);

        return true;
    }

    private String createNewContainer(Integer projectId, String ip, ImageType imageType) {
        CloudFileHelper.downloadFile(ip, projectId);

        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(portBindings)
                .withPublishAllPorts(false)
                .withMemory(300 * 1024 * 1024L)
                .withPrivileged(true)
                .withBinds(new Bind("/ProjectFiles/" + projectId, new Volume("/code")));

        DockerClient dockerClient = DockerClientFactory.getDockerClient(ip);
        CreateContainerResponse response = dockerClient
                .createContainerCmd(imageType.getImageName())
                .withEnv(String.format("HOST_IP=%s", ip))
                .withExposedPorts(exposedPortList)
                .withHostConfig(hostConfig)
                .exec();

        return response.getId().substring(0, 12);
    }

    /**
     * 1. upload local file to cloud and remove special container
     * 2. stop keeper which is monitoring it
     * 3. reset record of the container in db
     */
    @Override
    public Boolean stopContainer(Integer projectId) {
        Project project = projectService.getProjectById(projectId);
        if ("null".equals(project.getIp())) return true;

        // upload local file to cloud and remove special container
        CloudFileHelper.uploadFile(project.getIp(), projectId);
        DockerClient dockerClient = DockerClientFactory.getDockerClient(project.getIp());
        dockerClient.removeContainerCmd(project.getContainerId()).withForce(true).exec();

        // stop keeper which is monitoring it
        keeper.removeKeep(projectId);

        // reset record of the container in db
        project.setIp("null");
        project.setContainerId("null");
        projectService.updateProject(project);

        return true;
    }

    /**
     * 1. remove bind record of the container in db
     */
    @Transactional
    @Override
    public Boolean destroyContainer(Integer userId, Integer projectId) {
        // remove bind record of the container in db
        projectService.unbindProjectFromUser(userId, projectId);

        return true;
    }

    private Map<Integer, Integer> getBindingPortsOfContainer(String ip, String containerId) {
        Map<Integer, Integer> result = attemptGetBindingPortsOfContainer(ip, containerId);

        while (result.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = attemptGetBindingPortsOfContainer(ip, containerId);
        }

        return result;
    }

    private Map<Integer, Integer> attemptGetBindingPortsOfContainer(String ip, String containerId) {
        Map<Integer, Integer> portMap = new HashMap<>();

        try {
            Process process = Runtime.getRuntime().exec(String.format("docker -H tcp://%s:2376 port %s", ip, containerId));
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                addPortMapEntry(line, portMap);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return portMap;
    }

    private void addPortMapEntry(String originalInfo, Map<Integer, Integer> portMap) {
        String[] infos = originalInfo.split(" ");

        Integer innerPort = Integer.parseInt(infos[0].split("/")[0]);
        Integer hostPort = Integer.parseInt(infos[2].split(":")[1]);

        portMap.put(innerPort, hostPort);
    }
}