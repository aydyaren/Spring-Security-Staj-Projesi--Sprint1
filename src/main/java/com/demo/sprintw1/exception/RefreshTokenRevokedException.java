package com.demo.sprintw1.exception;

/*
 * İptal edilmiş Refresh Token tekrar kullanılmak istendiğinde fırlatılır.
 */
public class RefreshTokenRevokedException extends RuntimeException {

    public RefreshTokenRevokedException(String message) {
        super(message);
    }

}