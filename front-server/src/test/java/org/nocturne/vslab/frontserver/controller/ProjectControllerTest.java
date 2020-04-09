package org.nocturne.vslab.frontserver.controller;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProjectControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void infoTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/project/info")
                .param("user_id", "3");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        System.out.println(content);
    }

    @Test
    void newTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/project/new")
                .param("user_id", "3")
                .param("project_name", "test")
                .param("project_type", "JAVA");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        System.out.println(content);
    }

    @Test
    void enterTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/project/enter")
                .param("project_id", "9");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        System.out.println(content);
    }

    @Test
    void exitTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/project/exit")
                .param("project_id", "9");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        System.out.println(content);
    }

    @Test
    void deleteTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/project/delete")
                .param("project_id", "9");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        System.out.println(content);
    }
}