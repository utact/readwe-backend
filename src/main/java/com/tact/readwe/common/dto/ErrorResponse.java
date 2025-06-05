package com.tact.readwe.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tact.readwe.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,
        int statusCode,
        String message,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(
                status.getReasonPhrase(),
                status.value(),
                message,
                LocalDateTime.now());
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.name(),
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }
}
