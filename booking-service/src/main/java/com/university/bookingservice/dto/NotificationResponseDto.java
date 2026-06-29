package com.university.bookingservice.dto;

import com.university.bookingservice.model.NotificationStatus;
import com.university.bookingservice.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDto(Long id, Long bookingId, Long userId,
                                       String message, NotificationType type,
                                       NotificationStatus status, LocalDateTime createdAt) {
}
