package org.nocturne.vslab.frontserver;

import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DirManager;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.api.manager.FileManager;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VslabFrontserverApplicationTests {

    @Reference(interfaceName = "dockerManager")
    public DockerManager dockerManager;

    @Test
    void contextLoads() {
        dockerManager.createContainer(3, "admin_container", ImageType.JAVA);
    }

}
