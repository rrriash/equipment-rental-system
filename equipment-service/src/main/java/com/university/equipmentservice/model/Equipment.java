package com.university.equipmentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    private Long id;
    private String name;
    private EquipmentType type;
    private String description;
    private String inventoryNumber;
    private EquipmentStatus status;
    private Long categoryId;
}
