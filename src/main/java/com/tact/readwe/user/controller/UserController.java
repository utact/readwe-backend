package com.tact.readwe.user.controller;

import com.tact.readwe.auth.dto.AuthResponse;
import com.tact.readwe.common.dto.ApiResponse;
import com.tact.readwe.user.dto.UserSignUpRequest;
import com.tact.readwe.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> signUp(@Valid @RequestBody UserSignUpRequest request) {
        AuthResponse response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED, response));
    }
}
