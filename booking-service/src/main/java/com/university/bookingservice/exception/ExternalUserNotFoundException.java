package com.university.bookingservice.exception;

public class ExternalUserNotFoundException extends RuntimeException {

    private final String details;

    public ExternalUserNotFoundException(Long userId) {
        super("Пользователь не найден");
        this.details = "Пользователь с id = " + userId + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
