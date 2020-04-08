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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

//@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void registerTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/user/register")
                .param("user_name", "root")
                .param("password", "root");

        String content = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        assertTrue(content.contains("true"));
    }

    @Test
    void authTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/user/auth")
                .param("user_name", "admin")
                .param("password", "admin");

        String content = mockMvc.perform(request)
                .andDo(System.out::println)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        assertTrue(content.contains("true"));
    }

    @Test
    void updateTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/user/info_update")
                .param("user_id", "3")
                .param("user_name", "admin")
                .param("password", "admin");

        String content = mockMvc.perform(request)
                .andDo(System.out::println)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        assertTrue(content.contains("admin"));
    }

    @Test
    void infoTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user/info")
                .param("user_name", "admin");

        String content = mockMvc.perform(request)
                .andDo(System.out::println)
                .andReturn().getResponse().getContentAsString(Charsets.UTF_8);

        assertTrue(content.contains("3"));
    }
}