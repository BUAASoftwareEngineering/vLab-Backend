package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.User;
import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.exceptiion.user.WrongCaptchaException;
import org.nocturne.vslab.backend.service.UserService;
import org.nocturne.vslab.backend.util.EmailCaptchaPool;
import org.nocturne.vslab.backend.util.UserTokenPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.nocturne.vslab.backend.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserTokenPool userTokenPool;
    private final EmailCaptchaPool captchaPool;

    @Autowired
    public UserController(UserService userService,
                          UserTokenPool userTokenPool,
                          EmailCaptchaPool captchaPool) {
        this.userService = userService;
        this.userTokenPool = userTokenPool;
        this.captchaPool = captchaPool;
    }

    @PostMapping("/login")
    public Result login(HttpServletResponse response,
                        @RequestParam(PARAM_USER_NAME) String username,
                        @RequestParam(PARAM_USER_PASSWORD) String password) {
        userService.authorizeUser(username, password);

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
                           @RequestParam(PARAM_USER_PASSWORD) String password,
                           @RequestParam(PARAM_USER_EMAIL) String email,
                           @RequestParam("captcha") String captcha) {
        if (!captchaPool.isCaptchaAccepted(email, captcha)) {
            throw new WrongCaptchaException();
        }

        User user = new User(null, username, DigestUtils.md5DigestAsHex(password.getBytes()), email);
        userService.createUser(user);

        return new Result(0, "注册成功", null);
    }

    @PostMapping("/info_update")
    public Result infoUpdate(HttpServletResponse response,
                             @CookieValue(PARAM_USER_ID) Integer userId,
                             @RequestParam(value = PARAM_USER_NAME, required = false) String username,
                             @RequestParam(value = PARAM_USER_PASSWORD, required = false) String password) {
        User user = new User(userId, username, password == null ? null : DigestUtils.md5DigestAsHex(password.getBytes()), null);
        userService.updateUser(user);

        setCookie(response, COOKIE_USER_NAME, user.getName());
        return new Result(0, "修改成功", user);
    }

    @GetMapping("/info")
    public Result info(@CookieValue(PARAM_USER_ID) Integer userId) {
        User user = userService.getUserById(userId);
        return new Result(0, "查询成功", user);
    }

    @PostMapping("/reset")
    public Result reset(@RequestParam(PARAM_USER_EMAIL) String email,
                        @RequestParam("captcha") String captcha,
                        @RequestParam(PARAM_USER_PASSWORD) String password) {
        if (!captchaPool.isCaptchaAccepted(email, captcha)) {
            throw new WrongCaptchaException();
        }

        User user = userService.getUserByEmail(email);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        userService.updateUser(user);

        return new Result(0, "更新成功", user);
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
