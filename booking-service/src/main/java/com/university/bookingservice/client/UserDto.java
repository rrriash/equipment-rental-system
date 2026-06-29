package com.university.bookingservice.client;

public record UserDto(Long id, String fullName, String email, String role, String status) {
}
