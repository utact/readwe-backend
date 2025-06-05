package com.tact.readwe.auth.controller;

import com.tact.readwe.auth.dto.*;
import com.tact.readwe.auth.service.AuthService;
import com.tact.readwe.common.dto.ApiResponse;
import com.tact.readwe.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest request) {
        AuthService.Tokens tokens = authService.login(request.email(), request.password());
        UserLoginResponse response = new UserLoginResponse(tokens.accessToken(), tokens.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(@RequestBody RefreshRequest request) {
        String newAccessToken = authService.refreshAccessToken(request.userId(), request.refreshToken());
        AccessTokenResponse response = new AccessTokenResponse(newAccessToken);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    // TODO: 액세스 토큰 블랙리스트 처리 + 파라미터 변경 (@AuthenticationPrincipal 활용)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody UserLogoutRequest request) {
        authService.logout(request.userId());
        return ResponseEntity.noContent().build();
    }
}
