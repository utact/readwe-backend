package com.tact.readwe.auth.repository;

public interface RefreshTokenRepository {
    void save(String userId, String refreshToken);
}
