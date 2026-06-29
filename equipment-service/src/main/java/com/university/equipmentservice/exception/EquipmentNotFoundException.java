package com.university.equipmentservice.exception;

public class EquipmentNotFoundException extends RuntimeException {

    private final String details;

    public EquipmentNotFoundException(Long id) {
        super("Оборудование не найдено");
        this.details = "Оборудование с id = " + id + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
