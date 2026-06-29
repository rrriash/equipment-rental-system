package com.university.bookingservice.exception;

import com.university.bookingservice.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBookingNotFound(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("BOOKING_NOT_FOUND", ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(ExternalUserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalUserNotFound(ExternalUserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("USER_NOT_FOUND", ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserBlocked(UserBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto("USER_BLOCKED", ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(ExternalEquipmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalEquipmentNotFound(ExternalEquipmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("EQUIPMENT_NOT_FOUND", ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(EquipmentNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleEquipmentNotAvailable(EquipmentNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("EQUIPMENT_NOT_AVAILABLE", ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleBookingConflict(BookingConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(ex.getCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidBookingOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOperation(InvalidBookingOperationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("INVALID_BOOKING_OPERATION", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidBookingPeriodException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPeriod(InvalidBookingPeriodException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("INVALID_BOOKING_PERIOD", ex.getMessage(), null));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleServiceUnavailable(ExternalServiceException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDto("SERVICE_UNAVAILABLE", ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("VALIDATION_ERROR", "Некорректные данные запроса", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("INTERNAL_ERROR", "Внутренняя ошибка сервера", ex.getMessage()));
    }
}
