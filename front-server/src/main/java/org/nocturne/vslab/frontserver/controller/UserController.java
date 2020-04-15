package org.nocturne.vslab.frontserver.controller;

import org.nocturne.vslab.api.entity.User;
import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.service.UserService;
import org.nocturne.vslab.frontserver.util.UserTokenPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.nocturne.vslab.frontserver.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserTokenPool userTokenPool;

    @Autowired
    public UserController(UserService userService,
                          UserTokenPool userTokenPool) {
        this.userService = userService;
        this.userTokenPool = userTokenPool;
    }

    @PostMapping("/login")
    public Result login(HttpServletResponse response,
                        @RequestParam(PARAM_USER_NAME) String username,
                        @RequestParam(PARAM_USER_PASSWORD) String password) {
        userService.authorizeUser(username, password);
        System.out.println("12321");

        User user = userService.getUserByName(username);
        setCookie(response, COOKIE_USER_ID, user.getId().toString());
        setCookie(response, COOKIE_USER_NAME, user.getName());
        setCookie(response, COOKIE_USER_TOKEN, userTokenPool.generateToken(user.getId()));
        return new Result(0, "登录成功", user);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletResponse response) {
        removeCookie(response, COOKIE_USER_ID);
        removeCookie(response, COOKIE_USER_NAME);
        removeCookie(response, COOKIE_USER_TOKEN);

        return new Result(0, "登出成功", null);
    }

    @PostMapping("/register")
    public Result register(@RequestParam(PARAM_USER_NAME) String username,
                           @RequestParam(PARAM_USER_PASSWORD) String password) {
        User user = new User(null, username, DigestUtils.md5DigestAsHex(password.getBytes()));
        userService.createUser(user);

        return new Result(0, "注册成功", null);
    }

    @PostMapping("/info_update")
    public Result infoUpdate(HttpServletResponse response,
                             @CookieValue(PARAM_USER_ID) Integer userId,
                             @RequestParam(value = PARAM_USER_NAME, required = false) String username,
                             @RequestParam(value = PARAM_USER_PASSWORD, required = false) String password) {
        User user = new User(userId, username, DigestUtils.md5DigestAsHex(password.getBytes()));
        userService.updateUser(user);

        setCookie(response, COOKIE_USER_NAME, user.getName());
        return new Result(0, "修改成功", user);
    }

    @GetMapping("/info")
    public Result info(@CookieValue(PARAM_USER_ID) Integer userId) {
        User user = userService.getUserById(userId);
        return new Result(0, "查询成功", user);
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        removeCookie(response, cookieName, "/");
    }

    private void removeCookie(HttpServletResponse response, String cookieName, String path) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void setCookie(HttpServletResponse response, String cookieName, String value) {
        setCookie(response, cookieName, value, "/");
    }

    private void setCookie(HttpServletResponse response, String cookieName, String value, String path) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
}