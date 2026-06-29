package com.university.equipmentservice.dto;

import com.university.equipmentservice.model.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentRequestDto {

    @NotBlank(message = "Equipment name is required")
    private String name;

    @NotNull(message = "Equipment type is required")
    private EquipmentType type;

    private String description;

    @NotBlank(message = "Inventory number is required")
    private String inventoryNumber;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}
