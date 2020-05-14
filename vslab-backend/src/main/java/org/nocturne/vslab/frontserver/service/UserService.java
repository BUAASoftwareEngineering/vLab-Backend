package org.nocturne.vslab.frontserver.service;

import org.nocturne.vslab.frontserver.bean.User;
import org.nocturne.vslab.frontserver.exceptiion.user.UserAuthFailException;
import org.nocturne.vslab.frontserver.exceptiion.user.UserNotFoundException;
import org.nocturne.vslab.frontserver.exceptiion.user.UsernameAlreadyExist;
import org.nocturne.vslab.frontserver.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Objects;

@Service
public class UserService {

    private UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserByName(String username) {
        try {
            return Objects.requireNonNull(userMapper.getUserByName(username));
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public User getUserById(Integer id) {
        try {
            return Objects.requireNonNull(userMapper.getUserById(id));
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public void createUser(User user) {
        try {
            userMapper.createUser(user);
        } catch (Exception e) {
            throw new UsernameAlreadyExist();
        }
    }

    public void updateUser(User user) {
        try {
            userMapper.updateUser(user);
        } catch (Exception e) {
            throw new UsernameAlreadyExist();
        }
    }

    public void authorizeUser(String username, String password) {
        try {
            User expectedUser = Objects.requireNonNull(userMapper.getUserByName(username));
            String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

            if (!expectedUser.getPassword().equals(encryptedPassword)) {
                throw new UserAuthFailException();
            }
        } catch (Exception e) {
            throw new UserAuthFailException();
        }
    }
}
