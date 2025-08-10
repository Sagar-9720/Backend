package com.travelmate.authservice.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {
        super(message);
    }
    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmailNotFoundException() {
        super("Email not found");
    }
}
