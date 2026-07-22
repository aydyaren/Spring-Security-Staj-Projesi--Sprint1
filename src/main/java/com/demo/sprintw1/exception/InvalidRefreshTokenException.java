package com.demo.sprintw1.exception;

/*
 * Gönderilen Refresh Token veritabanında bulunamadığında fırlatılır.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }

}
