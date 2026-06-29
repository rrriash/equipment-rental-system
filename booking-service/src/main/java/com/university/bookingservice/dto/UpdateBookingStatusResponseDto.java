package com.university.bookingservice.dto;

import com.university.bookingservice.model.BookingStatus;

public record UpdateBookingStatusResponseDto(Long id, BookingStatus status, String message) {
}
