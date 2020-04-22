package org.nocturne.vslab.backserver.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.apache.dubbo.config.annotation.Service;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.backserver.docker.DockerClientFactory;
import org.nocturne.vslab.backserver.docker.DockerHostConfig;
import org.nocturne.vslab.backserver.mapper.ContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "dockerManager")
@Component
public class DockerManagerImpl implements DockerManager {

    private static final Integer CONTAINER_SERVER_PORT = 3000;
    private static final Integer TERMINAL_PORT = 4000;
    private static final Integer LANGUAGE_PORT = 5000;

    private ContainerMapper containerMapper;
    private static List<ExposedPort> exposedPortList = new ArrayList<>();
    private static HostConfig hostConfig;

    static {
        exposedPortList.add(ExposedPort.tcp(CONTAINER_SERVER_PORT));
        exposedPortList.add(ExposedPort.tcp(TERMINAL_PORT));
        exposedPortList.add(ExposedPort.tcp(LANGUAGE_PORT));

        Ports portBindings = new Ports();
        portBindings.bind(exposedPortList.get(0), Ports.Binding.empty());
        portBindings.bind(exposedPortList.get(1), Ports.Binding.empty());
        portBindings.bind(exposedPortList.get(2), Ports.Binding.empty());

        hostConfig = HostConfig.newHostConfig()
                .withPortBindings(portBindings)
                .withPublishAllPorts(false)
                .withMemory(200 * 1024 * 1024L);
    }

    @Autowired
    public DockerManagerImpl(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @Transactional
    @Override
    public Container createContainer(Integer userId, String containerName, ImageType imageType) {
        String ip = DockerHostConfig.getIPRandomly();

        DockerClient dockerClient = DockerClientFactory.getDockerClient(ip);
        CreateContainerResponse response = dockerClient
                .createContainerCmd(imageType.getImageName())
                .withExposedPorts(exposedPortList)
                .withHostConfig(hostConfig)
                .exec();
        String containerId = response.getId().substring(0, 12);

        Container container = new Container(null, containerId, userId,
                imageType, containerName,
                ip, 0, 0, 0);

        containerMapper.createContainer(container);

        return container;
    }

    @Transactional
    @Override
    public Boolean startContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);

        DockerClient dockerClient = DockerClientFactory.getDockerClient(container.getIp());
        dockerClient.startContainerCmd(container.getContainerId()).exec();

        Map<Integer, Integer> portMap = getBindingPortsOfContainer(container.getIp(), container.getContainerId());
        while (portMap.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            portMap = getBindingPortsOfContainer(container.getIp(), container.getContainerId());
        }

        container.setServerPort(portMap.get(CONTAINER_SERVER_PORT));
        container.setTerminalPort(portMap.get(TERMINAL_PORT));
        container.setLanguagePort(portMap.get(LANGUAGE_PORT));

        containerMapper.updateContainer(container);
        return true;
    }

    @Override
    public Boolean stopContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);
        DockerClient dockerClient = DockerClientFactory.getDockerClient(container.getIp());
        dockerClient.stopContainerCmd(container.getContainerId()).exec();

        return true;
    }

    @Transactional
    @Override
    public Boolean destroyContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);

        DockerClient dockerClient = DockerClientFactory.getDockerClient(container.getIp());
        dockerClient.removeContainerCmd(container.getContainerId())
                .withForce(true)
                .exec();

        containerMapper.deleteContainer(projectId);

        return true;
    }

    private Map<Integer, Integer> getBindingPortsOfContainer(String ip, String containerId) {
        Map<Integer, Integer> portMap = new HashMap<>();

        try {
            Process process = Runtime.getRuntime().exec(String.format("docker --tlsverify --tlscacert=./certs/ca.pem --tlscert=./certs/cert.pem --tlskey=./certs/key.pem -H tcp://%s:2376 port %s", ip, containerId));
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