package com.tact.readwe.user.service;

import com.tact.readwe.auth.dto.AuthResponse;
import com.tact.readwe.auth.service.AuthService;
import com.tact.readwe.global.exception.BusinessException;
import com.tact.readwe.global.exception.ErrorCode;
import com.tact.readwe.user.dto.UserSignUpRequest;
import com.tact.readwe.user.entity.User;
import com.tact.readwe.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional
    public AuthResponse signUp(UserSignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(User.signUpOf(request.name(), request.email(), encodedPassword));
        AuthService.Tokens response = authService.login(request.email(), request.password());

        return new AuthResponse(user.getUserId(), response.accessToken(), response.refreshToken());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
