package com.tact.readwe.auth.repository;

public interface AccessTokenBlacklistRepository {
    void save(String accessToken, long expirationDate);
    boolean isBlacklisted(String accessToken);
    void delete(String accessToken);
}
