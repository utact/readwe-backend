package com.tact.readwe.auth.service;

import com.tact.readwe.auth.token.TokenProvider;
import com.tact.readwe.auth.token.TokenValidator;
import com.tact.readwe.global.exception.BusinessException;
import com.tact.readwe.global.exception.ErrorCode;
import com.tact.readwe.user.entity.User;
import com.tact.readwe.user.service.UserService;
import com.tact.readwe.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserService userService,
                       TokenProvider tokenProvider,
                       TokenValidator tokenValidator,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.tokenValidator = tokenValidator;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public Tokens login(String email, String rawPassword) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

        if (!userService.matchesPassword(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        String accessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

        refreshTokenRepository.save(user.getUserId(), refreshToken);

        return new Tokens(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String userId, String refreshToken) {
        if (!tokenValidator.isValid(refreshToken)) {
            throw new BusinessException(ErrorCode.MALFORMED_REFRESH_TOKEN);
        }

        if (tokenValidator.isExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        refreshTokenRepository.findByUserId(userId)
                .filter(token -> token.equals(refreshToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        User user = userService.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.delete(userId);
    }

    public record Tokens(String accessToken, String refreshToken) {
    }
}
