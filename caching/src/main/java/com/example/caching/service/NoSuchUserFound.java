package com.example.caching.service;

public class NoSuchUserFound extends Exception {
    public NoSuchUserFound(String message) {
        super(message);
    }
}
