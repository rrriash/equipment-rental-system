package com.university.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    private final String details;

    public EmailAlreadyExistsException(String email) {
        super("Email уже используется");
        this.details = "Пользователь с email \"" + email + "\" уже существует";
    }

    public String getDetails() {
        return details;
    }
}
