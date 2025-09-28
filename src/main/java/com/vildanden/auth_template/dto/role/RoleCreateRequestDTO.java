package com.vildanden.auth_template.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear nuevo rol
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RoleCreateRequestDTO(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre del rol no puede exceder 50 caracteres")
        String name,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String description
) {}