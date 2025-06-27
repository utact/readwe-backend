package com.tact.readwe.auth.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

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

    @Override
    public Optional<String> findByUserId(String userId) {
        String token = redisTemplate.opsForValue().get(getKey(userId));
        return Optional.ofNullable(token);
    }

    @Override
    public void delete(String userId) {
        redisTemplate.delete(getKey(userId));
    }

    private String getKey(String userId) {
        return "refresh:" + userId;
    }
}
