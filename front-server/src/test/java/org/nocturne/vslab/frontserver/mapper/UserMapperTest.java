package org.nocturne.vslab.frontserver.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.api.entity.User;
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
class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Test
    void getUserByNameExist() {
        User admin = userMapper.getUserByName("admin");
        assertNotNull(admin);
    }

    @Test
    void getUserByNameNotExist() {
        assertNull(userMapper.getUserByName("nullllllllllll"));
    }

    @Test
    void getUserByIdExist() {
        User user = userMapper.getUserById(57);
        assertNotNull(user);
    }

    @Test
    void createUser() {
        userMapper.createUser(new User(null, "nulllllllllll", "nulllllllllll"));
        assertNotNull(userMapper.getUserByName("nulllllllllll"));
    }

    @Test
    void deleteUserByName() {
        userMapper.deleteUserByName("admin");
        assertNull(userMapper.getUserByName("admin"));
    }

    @Test
    void updateUser() {
        User admin = userMapper.getUserByName("admin");
        admin.setPassword("test");
        userMapper.updateUser(admin);

        admin = userMapper.getUserByName("admin");
        assertEquals("test", admin.getPassword());
    }
}