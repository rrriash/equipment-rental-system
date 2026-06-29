package com.university.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    private final String details;

    public UserNotFoundException(Long id) {
        super("Пользователь не найден");
        this.details = "Пользователь с id = " + id + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
