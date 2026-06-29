package com.university.equipmentservice.exception;

public class InventoryNumberAlreadyExistsException extends RuntimeException {

    private final String details;

    public InventoryNumberAlreadyExistsException(String inventoryNumber) {
        super("Инвентарный номер уже занят");
        this.details = "Оборудование с инвентарным номером \"" + inventoryNumber + "\" уже существует";
    }

    public String getDetails() {
        return details;
    }
}
