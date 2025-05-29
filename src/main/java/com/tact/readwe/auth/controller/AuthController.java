package com.tact.readwe.auth.controller;

import com.tact.readwe.auth.dto.AccessResponse;
import com.tact.readwe.auth.dto.RefreshRequest;
import com.tact.readwe.auth.service.AuthService;
import com.tact.readwe.user.dto.UserLoginRequest;
import com.tact.readwe.user.dto.UserLoginResponse;
import com.tact.readwe.user.dto.UserLogoutRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        AuthService.Tokens tokens = authService.login(request.email(), request.password());
        UserLoginResponse response = new UserLoginResponse(tokens.accessToken(), tokens.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessResponse> refreshToken(@RequestBody RefreshRequest request) {
        String newAccessToken = authService.refreshAccessToken(request.userId(), request.refreshToken());
        AccessResponse response = new AccessResponse(newAccessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody UserLogoutRequest request) {
        authService.logout(request.userId());
        return ResponseEntity.noContent().build();
    }
}
