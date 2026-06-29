package com.university.bookingservice.service;

import com.university.bookingservice.dto.NotificationResponseDto;
import com.university.bookingservice.model.Notification;
import com.university.bookingservice.model.NotificationStatus;
import com.university.bookingservice.model.NotificationType;
import com.university.bookingservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Mock-отправка уведомления. Сохраняет запись со статусом SENT и выводит сообщение в лог.
     */
    public void sendNotification(Long userId, Long bookingId, NotificationType type, String message) {
        Notification notification = Notification.builder()
                .bookingId(bookingId)
                .userId(userId)
                .message(message)
                .type(type)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        log.info("[MOCK NOTIFICATION] userId={}, bookingId={}, type={}, message={}",
                userId, bookingId, type, message);
    }

    public List<NotificationResponseDto> getNotificationsByBookingId(Long bookingId) {
        return notificationRepository.findByBookingId(bookingId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponseDto toResponse(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getBookingId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
