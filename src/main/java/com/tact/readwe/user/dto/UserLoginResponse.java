package com.tact.readwe.user.dto;

public record UserLoginResponse(
        String accessToken,
        String refreshToken
) {
}
