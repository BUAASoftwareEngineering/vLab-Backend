package org.nocturne.vslab.backserver.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
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
import java.util.List;

@Service(interfaceName = "dockerManager")
@Component
public class DockerManagerImpl implements DockerManager {

    private ContainerMapper containerMapper;
    private static List<ExposedPort> exposedPortList = new ArrayList<>();
    private static Ports portBindings = new Ports();

    static {
        exposedPortList.add(ExposedPort.tcp(10000));
        exposedPortList.add(ExposedPort.tcp(20000));
        portBindings.bind(exposedPortList.get(0), Ports.Binding.empty());
        portBindings.bind(exposedPortList.get(1), Ports.Binding.empty());
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
                .createContainerCmd("vlab-base:1.0")
                .withExposedPorts(exposedPortList)
                .withEnv("MYSQL_ROOT_PASSWORD=123")
                .withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings).withPublishAllPorts(false))
                .exec();
        String containerId = response.getId().substring(0, 12);

        Container container = new Container(
                null,
                containerId,
                userId,
                imageType,
                containerName,
                ip,
                0,
                0);

        containerMapper.createContainer(container);

        return container;
    }

    @Transactional
    @Override
    public Boolean startContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);

        DockerClient dockerClient = DockerClientFactory.getDockerClient(container.getIp());
        dockerClient.startContainerCmd(container.getContainerId()).exec();

        List<Integer> portList = getBindingPortsOfContainer(container.getIp(), container.getContainerId());
        while (portList.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            portList = getBindingPortsOfContainer(container.getIp(), container.getContainerId());
        }

        container.setServerPort(portList.get(0));
        container.setTerminalPort(portList.get(1));

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

    private List<Integer> getBindingPortsOfContainer(String ip, String containerId) {
        List<Integer> portList = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(String.format("docker -H tcp://%s port %s", ip, containerId));
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                portList.add(getPortFromOriginalInfo(line));
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return portList;
    }

    private Integer getPortFromOriginalInfo(String originalInfo) {
        String mainInfo = originalInfo.split(" ")[2];
        return Integer.parseInt(mainInfo.split(":")[1]);
    }
}