package org.nocturne.vslab.frontserver.mapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.api.entity.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContainerMapperTest {

    @Autowired
    public ContainerMapper containerMapper;

    @Test
    public void getContainersOfUser() {
        List<Container> list = containerMapper.getContainersOfUser(57);
        assertFalse(list.isEmpty());
    }

    @Test
    public void getContainerById() {
        Container container = containerMapper.getContainerById(314);
        assertNotNull(container);
    }

    @Test
    public void updateContainerName() {
        Container container = containerMapper.getContainerById(314);
        assertNotEquals("new", container.getName());
        containerMapper.updateContainerName(314, "new");

        container = containerMapper.getContainerById(314);
        assertEquals("new", container.getName());
    }
}