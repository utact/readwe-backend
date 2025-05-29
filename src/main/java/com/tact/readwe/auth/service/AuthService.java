package com.tact.readwe.auth.service;

import com.tact.readwe.auth.token.TokenProvider;
import com.tact.readwe.user.entity.User;
import com.tact.readwe.user.service.UserService;
import com.tact.readwe.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserService userService,
                       TokenProvider tokenProvider,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
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

    public record Tokens(String accessToken, String refreshToken) {
    }
}
