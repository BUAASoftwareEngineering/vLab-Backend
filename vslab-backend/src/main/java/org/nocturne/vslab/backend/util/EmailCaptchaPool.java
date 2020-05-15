package org.nocturne.vslab.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Component
public class EmailCaptchaPool {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public EmailCaptchaPool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized String generateCaptcha(String email) {
        String time = String.valueOf(System.currentTimeMillis());
        String captcha = DigestUtils.md5DigestAsHex(time.getBytes()).substring(0, 6);

        redisTemplate.opsForValue().set("captcha:" + email, captcha, 10, TimeUnit.MINUTES);
        return captcha;
    }

    public boolean isCaptchaAccepted(String email, String captcha) {
        String expectedToken = redisTemplate.opsForValue().get("captcha:" + email);
        return expectedToken != null && expectedToken.equals(captcha);
    }
}
