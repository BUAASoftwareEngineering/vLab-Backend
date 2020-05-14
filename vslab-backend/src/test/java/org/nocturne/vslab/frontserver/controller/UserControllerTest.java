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

@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void register() {
    }

    @Test
    void infoUpdate() {
    }

    @Test
    void info() {
    }
}