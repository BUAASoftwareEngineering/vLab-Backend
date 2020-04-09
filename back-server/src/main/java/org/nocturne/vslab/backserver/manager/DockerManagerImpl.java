package org.nocturne.vslab.backserver.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import org.apache.dubbo.config.annotation.Service;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.backserver.docker.DockerClientFactory;
import org.nocturne.vslab.backserver.mapper.ContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;

@Service(interfaceName = "dockerManager")
@Component
public class DockerManagerImpl implements DockerManager {

    private ContainerMapper containerMapper;

    @Autowired
    public DockerManagerImpl(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @Transactional
    @Override
    public Container createContainer(Integer userId, String containerName, ImageType imageType) {
        DockerClient dockerClient = DockerClientFactory.getDockerClient("");
        CreateContainerResponse response = dockerClient.createContainerCmd("hello-world:latest")
                .withName(containerName)
                .exec();
        System.out.println(response.getId());
        Container container = new Container(null, response.getId().substring(0, 12), userId, imageType, containerName, "127.0.0.1", 10000, 20000);
        containerMapper.createContainer(container);

        return container;
    }

    @Transactional
    @Override
    public Boolean destroyContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);
        DockerClient dockerClient = DockerClientFactory.getDockerClient("");
        dockerClient.removeContainerCmd(container.getContainerId())
                .withForce(true)
                .exec();

        containerMapper.deleteContainer(projectId);

        return true;
    }

    @Override
    public Boolean startContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);
        DockerClient dockerClient = DockerClientFactory.getDockerClient("");
        dockerClient.startContainerCmd(container.getContainerId()).exec();

        return true;
    }

    @Override
    public Boolean stopContainer(Integer projectId) {
        Container container = containerMapper.getContainerById(projectId);
        DockerClient dockerClient = DockerClientFactory.getDockerClient("");
        dockerClient.stopContainerCmd(container.getContainerId()).exec();

        return true;
    }
}
