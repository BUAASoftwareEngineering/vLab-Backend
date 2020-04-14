package org.nocturne.vslab.frontserver.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTokenPool {

    private ConcurrentHashMap<Integer, String> tokenDistributionData;

    public UserTokenPool() {
        tokenDistributionData = new ConcurrentHashMap<>();
    }

    public synchronized String generateToken(Integer userId) {
        String time;
        String token;

        do {
            time = String.valueOf(System.currentTimeMillis());
            token = DigestUtils.md5DigestAsHex(time.getBytes());
        } while (tokenDistributionData.containsValue(token));

        tokenDistributionData.put(userId, token);
        return token;
    }

    public boolean isUserTokenAccepted(Integer userId, String userToken) {
        if (!tokenDistributionData.containsKey(userId)) {
            return false;
        }

        return tokenDistributionData.get(userId).equals(userToken);
    }
}
