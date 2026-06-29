package com.university.equipmentservice.repository;

import com.university.equipmentservice.model.EquipmentCategory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EquipmentCategoryRepository {

    private final Map<Long, EquipmentCategory> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public EquipmentCategory save(EquipmentCategory category) {
        if (category.getId() == null) {
            category.setId(idCounter.getAndIncrement());
        }
        storage.put(category.getId(), category);
        return category;
    }

    public Optional<EquipmentCategory> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<EquipmentCategory> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
