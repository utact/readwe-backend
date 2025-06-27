package com.tact.readwe.auth.controller;

import com.tact.readwe.auth.dto.AuthResponse;
import com.tact.readwe.auth.dto.RefreshRequest;
import com.tact.readwe.auth.service.AuthService;
import com.tact.readwe.common.dto.ApiResponse;
import com.tact.readwe.user.dto.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        AuthService.Tokens tokens = authService.login(request.email(), request.password());

        AuthResponse response = new AuthResponse(
                tokens.userId(),
                tokens.accessToken(),
                tokens.refreshToken()
        );

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshTokens(@Valid @RequestBody RefreshRequest request) {
        AuthService.Tokens newTokens = authService.refreshTokens(request.refreshToken());

        AuthResponse response = new AuthResponse(
                newTokens.userId(),
                newTokens.accessToken(),
                newTokens.refreshToken()
        );

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UUID userId,
            HttpServletRequest request
    ) {
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            accessToken = authorizationHeader.substring(7);
        }

        authService.logout(userId, accessToken);

        return ResponseEntity.noContent().build();
    }
}
