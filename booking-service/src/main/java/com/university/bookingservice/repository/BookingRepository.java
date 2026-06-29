package com.university.bookingservice.repository;

import com.university.bookingservice.model.Booking;
import com.university.bookingservice.model.BookingStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {

    private static final Set<BookingStatus> ACTIVE_STATUSES = Set.of(
            BookingStatus.PENDING,
            BookingStatus.APPROVED,
            BookingStatus.ISSUED,
            BookingStatus.OVERDUE
    );

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idCounter.getAndIncrement());
        }
        storage.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Booking> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public long countActiveByUserId(Long userId) {
        return storage.values().stream()
                .filter(b -> b.getUserId().equals(userId) && ACTIVE_STATUSES.contains(b.getStatus()))
                .count();
    }

    public boolean hasActiveBookingForEquipmentInPeriod(Long equipmentId, LocalDate startDate, LocalDate endDate) {
        return storage.values().stream()
                .filter(b -> b.getEquipmentId().equals(equipmentId) && ACTIVE_STATUSES.contains(b.getStatus()))
                .anyMatch(b -> !b.getEndDate().isBefore(startDate) && !b.getStartDate().isAfter(endDate));
    }
}
