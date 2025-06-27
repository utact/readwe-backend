package com.tact.readwe.auth.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Date;

@Repository
public class RedisUsedRefreshTokenRepository implements UsedRefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_KEY_PREFIX = "used_jti:";

    public RedisUsedRefreshTokenRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String jti, Date expirationDate) {
        long expiresInMs = expirationDate.getTime() - System.currentTimeMillis();

        if (expiresInMs > 0) {
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + jti, "true", Duration.ofMillis(expiresInMs));
        }
    }

    @Override
    public boolean isUsed(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_PREFIX + jti));
    }

    @Override
    public void delete(String jti) {
    }
}
