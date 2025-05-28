package com.tact.readwe.user.dto;

public record UserSignUpRequest(
        String name,
        String email,
        String password) {
}
