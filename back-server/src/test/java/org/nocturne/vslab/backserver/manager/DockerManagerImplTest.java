package org.nocturne.vslab.backserver.manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DockerManagerImplTest {

    @Autowired
    public DockerManager dockerManager;

    @Test
    void createContainerTest() {
        dockerManager.createContainer(3, "temp", ImageType.JAVA);
    }

    @Test
    void startContainerTest() {
        dockerManager.startContainer(8);
    }

    @Test
    void stopContainerTest() {
        dockerManager.stopContainer(8);
    }

    @Test
    void destroyContainerTest() {
        dockerManager.destroyContainer(8);
    }
}