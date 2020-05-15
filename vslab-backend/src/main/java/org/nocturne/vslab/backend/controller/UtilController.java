package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.bean.User;
import org.nocturne.vslab.backend.mapper.UserMapper;
import org.nocturne.vslab.backend.util.EmailCaptchaPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/util")
public class UtilController {

    private final EmailCaptchaPool captchaPool;
    private final UserMapper userMapper;

    @Autowired
    public UtilController(EmailCaptchaPool captchaPool,
                          UserMapper userMapper) {
        this.captchaPool = captchaPool;
        this.userMapper = userMapper;
    }

    @GetMapping("/check-username")
    public Result checkUsername(@RequestParam("username") String username) {
        User user = userMapper.getUserByName(username);

        if (user == null) {
            return new Result(0, "用户名可用", Boolean.TRUE.toString());
        } else {
            return new Result(0, "用户名不可用", Boolean.FALSE.toString());
        }
    }

    @GetMapping("")

}
