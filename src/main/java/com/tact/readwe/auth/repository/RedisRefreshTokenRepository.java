package com.tact.readwe.auth.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static Duration tokenExpireTime;

    public RedisRefreshTokenRepository(StringRedisTemplate redisTemplate,
                                       @Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationMillis) {
        this.redisTemplate = redisTemplate;
        tokenExpireTime = Duration.ofMillis(refreshTokenExpirationMillis);
    }

    @Override
    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(getKey(userId), refreshToken, tokenExpireTime);
    }

    private String getKey(String userId) {
        return "refresh:" + userId;
    }
}
