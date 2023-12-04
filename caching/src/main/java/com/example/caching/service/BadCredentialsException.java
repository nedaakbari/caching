package com.example.caching.service;

public class BadCredentialsException extends Exception {
    public BadCredentialsException() {
        super("Unauthorized");
    }

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException(Exception cause) {
        super("Unauthorized", cause);
    }
}
