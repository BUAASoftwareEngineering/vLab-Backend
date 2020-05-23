package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.bean.User;
import org.nocturne.vslab.backend.mapper.UserMapper;
import org.nocturne.vslab.backend.util.EmailCaptchaPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/util")
public class UtilController {

    private final EmailCaptchaPool captchaPool;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;

    @Autowired
    public UtilController(EmailCaptchaPool captchaPool,
                          JavaMailSender mailSender,
                          UserMapper userMapper) {
        this.captchaPool = captchaPool;
        this.mailSender = mailSender;
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

    @GetMapping("/check-email")
    public Result checkEmail(@RequestParam("email") String email) {
        User user = userMapper.getUserByEmail(email);

        if (user == null) {
            return new Result(0, "邮箱可用", Boolean.TRUE.toString());
        } else {
            return new Result(0, "邮箱不可用", Boolean.FALSE.toString());
        }
    }

    @PostMapping("/send-captcha")
    public Result sendCaptchaEmail(@RequestParam("email") String email) {
        String captcha = captchaPool.generateCaptcha(email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vlab_team@yeah.net");
        message.setTo(email);
        message.setCc("vlab_team@yeah.net");
        message.setSubject("vlab为您竭诚服务");
        message.setText("很快我们就能见面啦，这是我们之间的见面暗号：" + captcha);

        mailSender.send(message);

        return new Result(0, "爷发完了", null);
    }
}
