package com.university.bookingservice.dto;

import com.university.bookingservice.model.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponseDto(Long id, Long userId, Long equipmentId,
                                  LocalDate startDate, LocalDate endDate,
                                  BookingStatus status, LocalDateTime createdAt) {
}
