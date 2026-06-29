package com.university.bookingservice.repository;

import com.university.bookingservice.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {

    private final Map<Long, Notification> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idCounter.getAndIncrement());
        }
        storage.put(notification.getId(), notification);
        return notification;
    }

    public List<Notification> findByBookingId(Long bookingId) {
        return storage.values().stream()
                .filter(n -> n.getBookingId().equals(bookingId))
                .collect(Collectors.toList());
    }
}
