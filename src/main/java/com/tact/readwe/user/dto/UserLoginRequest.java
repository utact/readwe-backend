package com.tact.readwe.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank @Size(max = 100) @Email String email,
        @NotBlank @Size(max = 100) String password
) {
}
