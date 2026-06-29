package com.university.equipmentservice.service;

import com.university.equipmentservice.dto.CreateEquipmentCategoryRequestDto;
import com.university.equipmentservice.dto.EquipmentCategoryResponseDto;
import com.university.equipmentservice.exception.CategoryNotFoundException;
import com.university.equipmentservice.model.EquipmentCategory;
import com.university.equipmentservice.repository.EquipmentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentCategoryService {

    private final EquipmentCategoryRepository categoryRepository;

    public EquipmentCategoryResponseDto createCategory(CreateEquipmentCategoryRequestDto request) {
        EquipmentCategory category = EquipmentCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public List<EquipmentCategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EquipmentCategoryResponseDto getCategoryById(Long id) {
        EquipmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toResponse(category);
    }

    private EquipmentCategoryResponseDto toResponse(EquipmentCategory category) {
        return new EquipmentCategoryResponseDto(category.getId(), category.getName(), category.getDescription());
    }
}
