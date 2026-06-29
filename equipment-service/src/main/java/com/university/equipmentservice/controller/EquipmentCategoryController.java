package com.university.equipmentservice.controller;

import com.university.equipmentservice.dto.CreateEquipmentCategoryRequestDto;
import com.university.equipmentservice.dto.EquipmentCategoryResponseDto;
import com.university.equipmentservice.service.EquipmentCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment-categories")
@RequiredArgsConstructor
public class EquipmentCategoryController {

    private final EquipmentCategoryService categoryService;

    @GetMapping
    public List<EquipmentCategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentCategoryResponseDto createCategory(@RequestBody @Valid CreateEquipmentCategoryRequestDto request) {
        return categoryService.createCategory(request);
    }
}
