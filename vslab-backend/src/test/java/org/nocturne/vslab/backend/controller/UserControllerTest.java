package org.nocturne.vslab.backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

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