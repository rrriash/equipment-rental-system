package com.university.equipmentservice.dto;

import com.university.equipmentservice.model.EquipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentStatusRequestDto {

    @NotNull(message = "Status is required")
    private EquipmentStatus status;
}
