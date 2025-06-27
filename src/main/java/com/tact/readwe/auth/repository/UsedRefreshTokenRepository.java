package com.tact.readwe.auth.repository;

import java.util.Date;

public interface UsedRefreshTokenRepository {
    void save(String jti, Date expirationDate);
    boolean isUsed(String jti);
    void delete(String jti);
}
