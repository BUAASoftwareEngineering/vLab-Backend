package org.nocturne.vslab.frontserver.controller;

import org.nocturne.vslab.api.entity.User;
import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private UserMapper userMapper;

    @Autowired
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public Result login(HttpServletResponse response,
                       @RequestParam("user_name") String username,
                       @RequestParam("password") String password) {
        try {
            User user = userMapper.getUserByName(username);
            String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

            if (encryptedPassword.equals(user.getPassword())) {
                response.addCookie(new Cookie("user_id", user.getId().toString()));
                response.addCookie(new Cookie("user_name", user.getName()));
                return new Result(true, "登录成功", user);
            }
        } catch (Exception ignored) {

        }

        return new Result(false, "用户名或密码错误", null);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletResponse response) {
        removeCookie(response, "user_id");
        removeCookie(response, "user_name");

        return new Result(true, "登出成功", null);
    }

    @PostMapping("/register")
    public Result register(@RequestParam("user_name") String username, @RequestParam("password") String password) {
        if (isUserExists(username)) {
            return new Result(false, "用户名已存在", null);
        }

        User user = new User(null, username, DigestUtils.md5DigestAsHex(password.getBytes()));
        userMapper.createUser(user);

        return new Result(true, "注册成功", null);
    }

    @PostMapping("/info_update")
    public Result infoUpdate(HttpServletResponse response,
                             @RequestParam("user_id") Integer userId,
                             @RequestParam("user_name") String username,
                             @RequestParam("password") String password) {
        if (!isUserExists(userId)) {
            return new Result(false, "用户不存在", null);
        }
        if (isUserExists(username)) {
            return new Result(false, "用户名已存在", null);
        }

        User user = new User(userId, username, DigestUtils.md5DigestAsHex(password.getBytes()));
        userMapper.updateUser(user);

        response.addCookie(new Cookie("user_name", user.getName()));
        return new Result(true, "修改成功", user);
    }

    @GetMapping("/info")
    public Result info(@RequestParam("user_id") Integer userId) {
        if (!isUserExists(userId)) {
            return new Result(false, "用户不存在", null);
        }

        User user = userMapper.getUserById(userId);
        return new Result(true, "查询成功", user);
    }

    private boolean isUserExists(String username) {
        try {
            User user = userMapper.getUserByName(username);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isUserExists(Integer userId) {
        try {
            User user = userMapper.getUserById(userId);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
