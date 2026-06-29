package com.university.bookingservice.exception;

public class UserBlockedException extends RuntimeException {

    private final String details;

    public UserBlockedException(Long userId) {
        super("Пользователь заблокирован");
        this.details = "Пользователь с id = " + userId + " имеет статус BLOCKED и не может создавать бронирования";
    }

    public String getDetails() {
        return details;
    }
}
