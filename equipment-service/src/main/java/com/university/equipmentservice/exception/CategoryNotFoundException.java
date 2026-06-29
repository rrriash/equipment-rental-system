package com.university.equipmentservice.exception;

public class CategoryNotFoundException extends RuntimeException {

    private final String details;

    public CategoryNotFoundException(Long id) {
        super("Категория оборудования не найдена");
        this.details = "Категория с id = " + id + " не существует";
    }

    public String getDetails() {
        return details;
    }
}
