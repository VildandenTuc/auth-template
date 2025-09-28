package com.vildanden.auth_template.dto.common;

/**
 * DTO para parámetros de paginación
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record PageRequestDTO(
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public PageRequestDTO {
        // Validaciones por defecto
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "id";
        if (sortDirection == null || (!sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC"))) {
            sortDirection = "ASC";
        }
    }

    public static PageRequestDTO of(int page, int size) {
        return new PageRequestDTO(page, size, "id", "ASC");
    }

    public static PageRequestDTO of(int page, int size, String sortBy) {
        return new PageRequestDTO(page, size, sortBy, "ASC");
    }
}