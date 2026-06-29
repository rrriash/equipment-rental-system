package com.university.bookingservice.client;

public record EquipmentDto(Long id, String name, String type, String description,
                            String inventoryNumber, String status, Long categoryId) {
}
