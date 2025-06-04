package com.tact.readwe.global.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("잘못된 이메일 또는 비밀번호입니다.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
