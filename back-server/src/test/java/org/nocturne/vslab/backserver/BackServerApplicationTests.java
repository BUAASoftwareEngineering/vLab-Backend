package org.nocturne.vslab.backserver;

import org.junit.jupiter.api.Test;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.frontserver.manager.DockerManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackServerApplicationTests {

    @Autowired
    public DockerManagerImpl dockerManager;

    @Test
    void contextLoads() {
        dockerManager.createContainer(3, "admin_container", ImageType.CPP);
    }

}
