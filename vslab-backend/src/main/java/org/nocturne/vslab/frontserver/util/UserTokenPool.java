package org.nocturne.vslab.frontserver.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTokenPool {

    private ConcurrentHashMap<Integer, String> tokenDistributionData;
    private ConcurrentHashMap<Integer, Long> timeoutMap;
    private static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 24;

    public UserTokenPool() {
        tokenDistributionData = new ConcurrentHashMap<>();
        timeoutMap = new ConcurrentHashMap<>();
    }

    public synchronized String generateToken(Integer userId) {
        String time;
        String token;

        do {
            time = String.valueOf(System.currentTimeMillis());
            token = DigestUtils.md5DigestAsHex(time.getBytes());
        } while (tokenDistributionData.containsValue(token));

        tokenDistributionData.put(userId, token);
        timeoutMap.put(userId, System.currentTimeMillis());
        return token;
    }

    public boolean isUserTokenAccepted(Integer userId, String userToken) {
        if (!tokenDistributionData.containsKey(userId)) {
            return false;
        }
        if (isExpired(userId)) {
            return false;
        }

        return tokenDistributionData.get(userId).equals(userToken);
    }

    private boolean isExpired(Integer userId) {
        if (!timeoutMap.containsKey(userId)) {
            return true;
        }

        return System.currentTimeMillis() - timeoutMap.get(userId) > EXPIRATION_TIME;
    }
}
