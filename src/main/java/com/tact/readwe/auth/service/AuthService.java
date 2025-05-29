package com.tact.readwe.auth.service;

import com.tact.readwe.auth.token.TokenProvider;
import com.tact.readwe.auth.token.TokenValidator;
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
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!userService.matchesPassword(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

        refreshTokenRepository.save(user.getUserId(), refreshToken);

        return new Tokens(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String userId, String refreshToken) {
        refreshTokenRepository.findByUserId(userId)
                .filter(token -> token.equals(refreshToken))
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (!tokenValidator.isValid(refreshToken)) {
            throw new RuntimeException("Malformed refresh token");
        }
        if (tokenValidator.isExpired(refreshToken)) {
            throw new RuntimeException("Expired refresh token");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return tokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.delete(userId);
    }

    public record Tokens(String accessToken, String refreshToken) {
    }
}
