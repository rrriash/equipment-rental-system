package com.university.userservice.dto;

import com.university.userservice.model.UserRole;
import com.university.userservice.model.UserStatus;

public record UserResponseDto(Long id, String fullName, String email, UserRole role, UserStatus status) {
}
