package org.nocturne.vslab.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.backend.bean.User;
import org.nocturne.vslab.backend.exceptiion.user.UserAuthFailException;
import org.nocturne.vslab.backend.exceptiion.user.UserNotFoundException;
import org.nocturne.vslab.backend.exceptiion.user.UserAlreadyExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void getUserByNameExist() {
        User admin = userService.getUserByName("admin");
        assertNotNull(admin);
    }

    @Test
    void  getUserByNameNotExist() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByName("nullllllllllllll");
        });
    }

    @Test
    void getUserByIdExist() {
        User user = userService.getUserById(57);
        assertNotNull(user);
    }

    @Test
    void getUserByIdNotExist() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1000);
        });
    }

    @Test
    void createUserNotDupName() {
        User user = new User(null, "nullllllllllllll", "nulllllllllll");
        userService.createUser(user);

        user = userService.getUserByName("nullllllllllllll");
        assertNotNull(user);
    }

    @Test
    void createUserDupName() {
        assertThrows(UserAlreadyExist.class, () -> {
            User user = new User(null, "admin", "admin");
            userService.createUser(user);
        });
    }

    @Test
    void updateUserNotDupName() {
        User user = new User(57, "admin2", "test");
        userService.updateUser(user);

        User admin2 = userService.getUserByName("admin2");
        assertNotNull(admin2);
    }

    @Test
    void updateUserDupName() {
        assertThrows(UserAlreadyExist.class, () -> {
            User user = new User(57, "admin1", "test");
            userService.updateUser(user);
        });
    }

    @Test
    void authorizeUserSuccess() {
        userService.authorizeUser("admin", "admin");
    }

    @Test
    void authorizeUserFail() {
        assertThrows(UserAuthFailException.class, () -> {
            userService.authorizeUser("admin", "wrong");
        });
    }
}