package com.university.equipmentservice.repository;

import com.university.equipmentservice.model.Equipment;
import com.university.equipmentservice.model.EquipmentStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class EquipmentRepository {

    private final Map<Long, Equipment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Equipment save(Equipment equipment) {
        if (equipment.getId() == null) {
            equipment.setId(idCounter.getAndIncrement());
        }
        storage.put(equipment.getId(), equipment);
        return equipment;
    }

    public Optional<Equipment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Equipment> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Equipment> findByStatus(EquipmentStatus status) {
        return storage.values().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean existsByInventoryNumber(String inventoryNumber) {
        return storage.values().stream()
                .anyMatch(e -> e.getInventoryNumber().equals(inventoryNumber));
    }

    public boolean existsByInventoryNumberAndIdNot(String inventoryNumber, Long id) {
        return storage.values().stream()
                .anyMatch(e -> e.getInventoryNumber().equals(inventoryNumber) && !e.getId().equals(id));
    }
}
