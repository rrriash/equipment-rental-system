package com.university.equipmentservice.controller;

import com.university.equipmentservice.dto.*;
import com.university.equipmentservice.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public List<EquipmentResponseDto> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    @GetMapping("/available")
    public List<EquipmentResponseDto> getAvailableEquipment() {
        return equipmentService.getAvailableEquipment();
    }

    @GetMapping("/{id}")
    public EquipmentResponseDto getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentResponseDto createEquipment(@RequestBody @Valid CreateEquipmentRequestDto request) {
        return equipmentService.createEquipment(request);
    }

    @PutMapping("/{id}")
    public EquipmentResponseDto updateEquipment(@PathVariable Long id,
                                                @RequestBody @Valid UpdateEquipmentRequestDto request) {
        return equipmentService.updateEquipment(id, request);
    }

    @PatchMapping("/{id}/status")
    public EquipmentResponseDto updateStatus(@PathVariable Long id,
                                             @RequestBody @Valid UpdateEquipmentStatusRequestDto request) {
        return equipmentService.updateStatus(id, request);
    }
}
