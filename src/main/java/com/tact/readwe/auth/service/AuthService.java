package com.tact.readwe.auth.service;

import com.tact.readwe.auth.repository.AccessTokenBlacklistRepository;
import com.tact.readwe.auth.repository.RefreshTokenRepository;
import com.tact.readwe.auth.repository.UsedRefreshTokenRepository;
import com.tact.readwe.auth.token.TokenProvider;
import com.tact.readwe.global.exception.BusinessException;
import com.tact.readwe.global.exception.ErrorCode;
import com.tact.readwe.user.entity.User;
import com.tact.readwe.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsedRefreshTokenRepository usedRefreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    public AuthService(UserService userService,
                       TokenProvider tokenProvider,
                       RefreshTokenRepository refreshTokenRepository,
                       UsedRefreshTokenRepository usedRefreshTokenRepository,
                       AccessTokenBlacklistRepository accessTokenBlacklistRepository) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.usedRefreshTokenRepository = usedRefreshTokenRepository;
        this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
    }

    @Transactional
    public Tokens login(String email, String rawPassword) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

        if (!userService.matchesPassword(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        return generateAndStoreTokens(user);
    }

    @Transactional
    public Tokens refreshTokens(String refreshToken) {
        if (tokenProvider.isTokenExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.MALFORMED_REFRESH_TOKEN);
        }

        UUID userIdFromToken = tokenProvider.getUserIdFromToken(refreshToken);
        String jtiFromToken = tokenProvider.getJtiFromToken(refreshToken);

        User user = userService.findById(String.valueOf(userIdFromToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String storedRefreshToken = refreshTokenRepository.findByUserId(userIdFromToken.toString())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedRefreshToken.equals(refreshToken)) {
            refreshTokenRepository.delete(userIdFromToken.toString());
            usedRefreshTokenRepository.save(jtiFromToken, tokenProvider.getExpirationDateFromToken(refreshToken));

            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (usedRefreshTokenRepository.isUsed(jtiFromToken)) {
            refreshTokenRepository.delete(userIdFromToken.toString());

            throw new BusinessException(ErrorCode.REUSED_REFRESH_TOKEN);
        }

        refreshTokenRepository.delete(user.getUserId().toString());
        usedRefreshTokenRepository.save(jtiFromToken, tokenProvider.getExpirationDateFromToken(refreshToken));

        String newAccessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

        refreshTokenRepository.save(user.getUserId().toString(), newRefreshToken);

        return new Tokens(user.getUserId(), user.getEmail(), newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(UUID userIdToLogout, String accessTokenToBlacklist) {
        refreshTokenRepository.delete(userIdToLogout.toString());

        Date expirationDate = tokenProvider.getExpirationDateFromToken(accessTokenToBlacklist);
        long expiresInMs = expirationDate.getTime() - System.currentTimeMillis();

        if (expiresInMs > 0) {
            accessTokenBlacklistRepository.save(accessTokenToBlacklist, expiresInMs);
        }
    }

    @Transactional
    public Tokens generateTokensForUser(User user) {
        return generateAndStoreTokens(user);
    }

    private Tokens generateAndStoreTokens(User user) {
        refreshTokenRepository.delete(user.getUserId().toString());

        String accessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

        refreshTokenRepository.save(user.getUserId().toString(), refreshToken);

        return new Tokens(user.getUserId(), user.getEmail(), accessToken, refreshToken);
    }

    public record Tokens(UUID userId, String email, String accessToken, String refreshToken) {
    }
}
