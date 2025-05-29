package com.tact.readwe.auth.dto;

public record RefreshRequest(
        String userId,
        String refreshToken
) {
}
