package com.university.bookingservice.controller;

import com.university.bookingservice.dto.BookingResponseDto;
import com.university.bookingservice.dto.CreateBookingRequestDto;
import com.university.bookingservice.dto.NotificationResponseDto;
import com.university.bookingservice.dto.UpdateBookingStatusResponseDto;
import com.university.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@RequestBody @Valid CreateBookingRequestDto request) {
        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingResponseDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/user/{userId}")
    public List<BookingResponseDto> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @PatchMapping("/{id}/approve")
    public UpdateBookingStatusResponseDto approveBooking(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @PatchMapping("/{id}/reject")
    public UpdateBookingStatusResponseDto rejectBooking(@PathVariable Long id) {
        return bookingService.rejectBooking(id);
    }

    @PatchMapping("/{id}/issue")
    public UpdateBookingStatusResponseDto issueBooking(@PathVariable Long id) {
        return bookingService.issueBooking(id);
    }

    @PatchMapping("/{id}/return")
    public UpdateBookingStatusResponseDto returnBooking(@PathVariable Long id) {
        return bookingService.returnBooking(id);
    }

    @PatchMapping("/{id}/cancel")
    public UpdateBookingStatusResponseDto cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @GetMapping("/{id}/notifications")
    public List<NotificationResponseDto> getNotifications(@PathVariable Long id) {
        return bookingService.getNotificationsForBooking(id);
    }
}
