package com.demo.sprintw1.exception;

/*
 * Refresh Token'ın süresi dolduğunda fırlatılır.
 */
public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException(String message) {
        super(message);
    }

}
