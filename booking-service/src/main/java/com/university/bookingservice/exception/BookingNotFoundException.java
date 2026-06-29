package com.university.bookingservice.exception;

public class BookingNotFoundException extends RuntimeException {

    private final String details;

    public BookingNotFoundException(Long id) {
        super("Бронирование не найдено");
        this.details = "Бронирование с id = " + id + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
