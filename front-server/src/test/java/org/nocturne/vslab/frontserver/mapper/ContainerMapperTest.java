package org.nocturne.vslab.frontserver.mapper;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContainerMapperTest {

    @Autowired
    public ContainerMapper containerMapper;

    @Test
    public void getContainersOfUserTest() {
        List<Container> containers = containerMapper.getContainersOfUser(3);
        System.out.println(containers);
    }
}