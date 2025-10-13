package com.khokhlov.cloudstorage.exception.auth;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String username) {
        super("Username already in use: " + username);
    }
}
