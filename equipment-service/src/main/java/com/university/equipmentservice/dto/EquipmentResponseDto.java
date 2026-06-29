package com.university.equipmentservice.dto;

import com.university.equipmentservice.model.EquipmentStatus;
import com.university.equipmentservice.model.EquipmentType;

public record EquipmentResponseDto(Long id, String name, EquipmentType type, String description,
                                   String inventoryNumber, EquipmentStatus status, Long categoryId) {
}
