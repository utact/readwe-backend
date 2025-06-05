package com.tact.readwe.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다.");
    }
}
