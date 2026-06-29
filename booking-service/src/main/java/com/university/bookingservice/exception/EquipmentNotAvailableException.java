package com.university.bookingservice.exception;

public class EquipmentNotAvailableException extends RuntimeException {

    private final String details;

    public EquipmentNotAvailableException(Long equipmentId, String currentStatus) {
        super("Оборудование недоступно для бронирования");
        this.details = "Оборудование с id = " + equipmentId + " имеет статус " + currentStatus
                + ", требуется статус AVAILABLE";
    }

    public String getDetails() {
        return details;
    }
}
