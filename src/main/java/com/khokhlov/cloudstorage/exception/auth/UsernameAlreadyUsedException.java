package com.khokhlov.cloudstorage.exception.auth;

public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException(String username) {
        super(messageFor(username));
    }

    public UsernameAlreadyUsedException(String username, Throwable cause) {
        super(messageFor(username), cause);
    }

    private static String messageFor(String username) {
        return "Username already in use: " + username;
    }

}
