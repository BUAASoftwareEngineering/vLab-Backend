package org.nocturne.vslab.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class UserTokenPool {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public UserTokenPool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized String generateToken(Integer userId) {
        String time;
        String token;

        do {
            time = String.valueOf(System.currentTimeMillis());
            token = DigestUtils.md5DigestAsHex(time.getBytes());
        } while (isTokenAlreadyInUsed(token));

        redisTemplate.opsForValue().set("token:" + userId, token, 3, TimeUnit.HOURS);
        return token;
    }

    private boolean isTokenAlreadyInUsed(String token) {
        Set<String> tokenKeys = redisTemplate.keys("token:*");
        if (tokenKeys == null || tokenKeys.isEmpty()) return false;

        List<String> tokens = redisTemplate.opsForValue().multiGet(tokenKeys);
        return tokens != null && tokens.contains(token);
    }

    public boolean isUserTokenAccepted(Integer userId, String userToken) {
        String expectedToken = redisTemplate.opsForValue().get("token:" + userId);
        return expectedToken != null && expectedToken.equals(userToken);
    }

}
