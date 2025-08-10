package com.travelmate.authservice.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
    public EmailAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmailAlreadyExistException() {
        super("Email already exists");
    }
}

