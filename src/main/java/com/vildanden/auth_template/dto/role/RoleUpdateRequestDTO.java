package com.vildanden.auth_template.dto.role;

import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar rol existente
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RoleUpdateRequestDTO(
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String description
) {}