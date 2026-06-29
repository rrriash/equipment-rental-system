package com.university.bookingservice.exception;

public class ExternalEquipmentNotFoundException extends RuntimeException {

    private final String details;

    public ExternalEquipmentNotFoundException(Long equipmentId) {
        super("Оборудование не найдено");
        this.details = "Оборудование с id = " + equipmentId + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
