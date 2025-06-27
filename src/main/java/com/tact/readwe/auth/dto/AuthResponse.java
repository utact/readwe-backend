package com.tact.readwe.auth.dto;

public record AuthResponse(
        Long userId,
        String accessToken,
        String refreshToken
) {
}
