package com.demo.sprintw1.exception;

// Kullanıcının sahip olduğu belgeler bulunduğunda fırlatılır.
public class UserHasDocumentsException extends RuntimeException {

    public UserHasDocumentsException(String message) {
        super(message);
    }

}
