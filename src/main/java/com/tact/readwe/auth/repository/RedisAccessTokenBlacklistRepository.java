package com.tact.readwe.auth.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisAccessTokenBlacklistRepository implements AccessTokenBlacklistRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_KEY_PREFIX = "blacklist_at:";

    public RedisAccessTokenBlacklistRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String accessToken, long expiresInMs) {
        if (expiresInMs > 0) {
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + accessToken, "true", Duration.ofMillis(expiresInMs));
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_PREFIX + accessToken));
    }

    @Override
    public void delete(String accessToken) {
    }
}
