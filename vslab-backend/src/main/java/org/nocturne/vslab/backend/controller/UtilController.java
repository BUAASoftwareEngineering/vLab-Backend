package org.nocturne.vslab.backend.controller;

import org.apache.ibatis.annotations.Param;
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
        message.setSubject("【vlab】注册验证码");
        message.setText("该邮箱正在被用于注册vlab账号，验证码为：" + captcha + "\n该验证码有效时间为10分钟，请及时完成注册");
        message.setFrom("vlab_team@163.com");
        message.setTo(email);

        mailSender.send(message);

        return new Result(0, "爷发完了", null);
    }
}
