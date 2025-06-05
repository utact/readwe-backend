package com.tact.readwe.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    MALFORMED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰 형식이 올바르지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
