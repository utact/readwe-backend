package com.tact.readwe.common.dto;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        int statusCode,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(true, status.value(), status.getReasonPhrase(), data);
    }
}
