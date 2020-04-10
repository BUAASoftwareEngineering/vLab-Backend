package org.nocturne.vslab.frontserver.util;

import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTokenPool {

    private ConcurrentHashMap<Integer, String> tokenDistributionData;
    private ConcurrentHashSet<String> acceptedTokens;

    public UserTokenPool() {
        tokenDistributionData = new ConcurrentHashMap<>();
        acceptedTokens = new ConcurrentHashSet<>();
    }

    public synchronized String generateToken(Integer userId) {
        String time;
        String token;

        do {
            time = String.valueOf(System.currentTimeMillis());
            token = DigestUtils.md5DigestAsHex(time.getBytes());
        } while (acceptedTokens.contains(token));

        if (tokenDistributionData.containsKey(userId)) {
            String oldToken = tokenDistributionData.get(userId);
            acceptedTokens.remove(oldToken);
        }
        tokenDistributionData.put(userId, token);
        return token;
    }

    public boolean isUserTokenAccepted(Integer userId, String userToken) {
        if (!tokenDistributionData.containsKey(userId) || !acceptedTokens.contains(userToken)) {
            return false;
        }

        return tokenDistributionData.get(userId).equals(userToken);
    }

}
