package com.university.equipmentservice.service;

import com.university.equipmentservice.dto.*;
import com.university.equipmentservice.exception.CategoryNotFoundException;
import com.university.equipmentservice.exception.EquipmentNotFoundException;
import com.university.equipmentservice.exception.InventoryNumberAlreadyExistsException;
import com.university.equipmentservice.model.Equipment;
import com.university.equipmentservice.model.EquipmentStatus;
import com.university.equipmentservice.repository.EquipmentCategoryRepository;
import com.university.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository categoryRepository;

    public EquipmentResponseDto createEquipment(CreateEquipmentRequestDto request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new CategoryNotFoundException(request.getCategoryId());
        }
        if (equipmentRepository.existsByInventoryNumber(request.getInventoryNumber())) {
            throw new InventoryNumberAlreadyExistsException(request.getInventoryNumber());
        }
        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .inventoryNumber(request.getInventoryNumber())
                .status(EquipmentStatus.AVAILABLE)
                .categoryId(request.getCategoryId())
                .build();
        return toResponse(equipmentRepository.save(equipment));
    }

    public List<EquipmentResponseDto> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EquipmentResponseDto> getAvailableEquipment() {
        return equipmentRepository.findByStatus(EquipmentStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EquipmentResponseDto getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
        return toResponse(equipment);
    }

    public EquipmentResponseDto updateEquipment(Long id, UpdateEquipmentRequestDto request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new CategoryNotFoundException(request.getCategoryId());
        }
        if (equipmentRepository.existsByInventoryNumberAndIdNot(request.getInventoryNumber(), id)) {
            throw new InventoryNumberAlreadyExistsException(request.getInventoryNumber());
        }

        equipment.setName(request.getName());
        equipment.setType(request.getType());
        equipment.setDescription(request.getDescription());
        equipment.setInventoryNumber(request.getInventoryNumber());
        equipment.setCategoryId(request.getCategoryId());
        return toResponse(equipmentRepository.save(equipment));
    }

    public EquipmentResponseDto updateStatus(Long id, UpdateEquipmentStatusRequestDto request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
        equipment.setStatus(request.getStatus());
        return toResponse(equipmentRepository.save(equipment));
    }

    private EquipmentResponseDto toResponse(Equipment equipment) {
        return new EquipmentResponseDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getDescription(),
                equipment.getInventoryNumber(),
                equipment.getStatus(),
                equipment.getCategoryId()
        );
    }
}
