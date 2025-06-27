package com.tact.readwe.user.dto;

import jakarta.validation.constraints.*;

public record UserSignUpRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 100) @Email String email,
        @NotBlank @Size(max = 100) String password) {
}
