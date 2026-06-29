package com.university.bookingservice.exception;

public class InvalidBookingPeriodException extends RuntimeException {

    public InvalidBookingPeriodException(String message) {
        super(message);
    }
}
