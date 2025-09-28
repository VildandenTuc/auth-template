package com.vildanden.auth_template.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de rol
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RoleResponseDTO(
        Long id,
        String name,
        String description,
        long userCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}