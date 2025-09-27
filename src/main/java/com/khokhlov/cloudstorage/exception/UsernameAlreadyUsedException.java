package com.khokhlov.cloudstorage.exception;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String username) {
        super("Username already in use: " + username);
    }
}
