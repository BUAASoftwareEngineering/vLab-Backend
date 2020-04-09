package org.nocturne.vslab.frontserver;

import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.nocturne.vslab.api.manager.DirManager;
import org.nocturne.vslab.api.manager.FileManager;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VslabFrontserverApplicationTests {

    @Reference(interfaceName = "fileManager")
    FileManager dirManager;

    @Test
    void contextLoads() {
        System.out.println(dirManager.createFile(1 , "1"));
    }

}
