package com.university.bookingservice.service;

import com.university.bookingservice.client.EquipmentDto;
import com.university.bookingservice.client.EquipmentServiceClient;
import com.university.bookingservice.client.UserDto;
import com.university.bookingservice.client.UserServiceClient;
import com.university.bookingservice.dto.BookingResponseDto;
import com.university.bookingservice.dto.CreateBookingRequestDto;
import com.university.bookingservice.dto.NotificationResponseDto;
import com.university.bookingservice.dto.UpdateBookingStatusResponseDto;
import com.university.bookingservice.exception.BookingConflictException;
import com.university.bookingservice.exception.BookingNotFoundException;
import com.university.bookingservice.exception.EquipmentNotAvailableException;
import com.university.bookingservice.exception.InvalidBookingOperationException;
import com.university.bookingservice.exception.InvalidBookingPeriodException;
import com.university.bookingservice.exception.UserBlockedException;
import com.university.bookingservice.model.Booking;
import com.university.bookingservice.model.BookingStatus;
import com.university.bookingservice.model.NotificationType;
import com.university.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int MAX_ACTIVE_BOOKINGS_PER_USER = 3;

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final UserServiceClient userServiceClient;
    private final EquipmentServiceClient equipmentServiceClient;

    public BookingResponseDto createBooking(CreateBookingRequestDto request) {
        long startTime = System.currentTimeMillis();
        try {
            // 1. Validate dates
            if (!request.getStartDate().isBefore(request.getEndDate())) {
                throw new InvalidBookingPeriodException("startDate должна быть раньше endDate");
            }

            // 2. Validate user exists and not blocked
            UserDto user = userServiceClient.getUserById(request.getUserId());
            if ("BLOCKED".equals(user.status())) {
                throw new UserBlockedException(request.getUserId());
            }

            // 3. Validate equipment exists and available
            EquipmentDto equipment = equipmentServiceClient.getEquipmentById(request.getEquipmentId());
            if (!"AVAILABLE".equals(equipment.status())) {
                throw new EquipmentNotAvailableException(request.getEquipmentId(), equipment.status());
            }

            // 4. Check active bookings limit per user
            if (bookingRepository.countActiveByUserId(request.getUserId()) >= MAX_ACTIVE_BOOKINGS_PER_USER) {
                throw new BookingConflictException("TOO_MANY_ACTIVE_BOOKINGS",
                        "У пользователя уже " + MAX_ACTIVE_BOOKINGS_PER_USER + " активных бронирований");
            }

            // 5. Check period conflict for the equipment
            if (bookingRepository.hasActiveBookingForEquipmentInPeriod(
                    request.getEquipmentId(), request.getStartDate(), request.getEndDate())) {
                throw new BookingConflictException("BOOKING_PERIOD_CONFLICT",
                        "Оборудование уже забронировано на указанный период");
            }

            // 6. Create booking
            Booking booking = Booking.builder()
                    .userId(request.getUserId())
                    .equipmentId(request.getEquipmentId())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .status(BookingStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            return toResponse(bookingRepository.save(booking));

        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("POST /bookings completed in {} ms", durationMs);
        }
    }

    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponseDto getBookingById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<BookingResponseDto> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UpdateBookingStatusResponseDto approveBooking(Long id) {
        Booking booking = findOrThrow(id);
        requireStatus(booking, BookingStatus.PENDING, "подтвердить");

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        // Update equipment status to BOOKED in equipment-service
        equipmentServiceClient.updateEquipmentStatus(booking.getEquipmentId(), "BOOKED");

        notificationService.sendNotification(
                booking.getUserId(), booking.getId(),
                NotificationType.BOOKING_APPROVED,
                "Ваша заявка #" + id + " подтверждена.");

        return new UpdateBookingStatusResponseDto(id, BookingStatus.APPROVED, "Бронирование подтверждено");
    }

    public UpdateBookingStatusResponseDto rejectBooking(Long id) {
        Booking booking = findOrThrow(id);
        requireStatus(booking, BookingStatus.PENDING, "отклонить");

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        notificationService.sendNotification(
                booking.getUserId(), booking.getId(),
                NotificationType.BOOKING_REJECTED,
                "Ваша заявка #" + id + " отклонена.");

        return new UpdateBookingStatusResponseDto(id, BookingStatus.REJECTED, "Бронирование отклонено");
    }

    public UpdateBookingStatusResponseDto issueBooking(Long id) {
        Booking booking = findOrThrow(id);
        requireStatus(booking, BookingStatus.APPROVED, "выдать");

        booking.setStatus(BookingStatus.ISSUED);
        bookingRepository.save(booking);

        // Update equipment status to ISSUED in equipment-service
        equipmentServiceClient.updateEquipmentStatus(booking.getEquipmentId(), "ISSUED");

        return new UpdateBookingStatusResponseDto(id, BookingStatus.ISSUED, "Оборудование выдано");
    }

    public UpdateBookingStatusResponseDto returnBooking(Long id) {
        Booking booking = findOrThrow(id);

        if (booking.getStatus() != BookingStatus.ISSUED && booking.getStatus() != BookingStatus.OVERDUE) {
            throw new InvalidBookingOperationException(
                    "Невозможно вернуть бронирование со статусом: " + booking.getStatus()
                    + ". Допустимые статусы: ISSUED, OVERDUE");
        }

        booking.setStatus(BookingStatus.RETURNED);
        bookingRepository.save(booking);

        // Return equipment to AVAILABLE in equipment-service
        equipmentServiceClient.updateEquipmentStatus(booking.getEquipmentId(), "AVAILABLE");

        return new UpdateBookingStatusResponseDto(id, BookingStatus.RETURNED, "Оборудование возвращено");
    }

    public UpdateBookingStatusResponseDto cancelBooking(Long id) {
        Booking booking = findOrThrow(id);
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new InvalidBookingOperationException(
                    "Невозможно отменить бронирование со статусом: " + booking.getStatus()
                    + ". Допустимые статусы: PENDING, APPROVED");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return new UpdateBookingStatusResponseDto(id, BookingStatus.CANCELLED, "Бронирование отменено");
    }

    public List<NotificationResponseDto> getNotificationsForBooking(Long id) {
        findOrThrow(id);
        return notificationService.getNotificationsByBookingId(id);
    }

    private Booking findOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    private void requireStatus(Booking booking, BookingStatus required, String operation) {
        if (booking.getStatus() != required) {
            throw new InvalidBookingOperationException(
                    "Невозможно " + operation + " бронирование со статусом: " + booking.getStatus()
                    + ". Требуемый статус: " + required);
        }
    }

    private BookingResponseDto toResponse(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getUserId(),
                booking.getEquipmentId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
