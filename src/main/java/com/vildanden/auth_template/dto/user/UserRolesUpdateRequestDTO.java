package com.vildanden.auth_template.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * DTO para actualizar roles de usuario (solo admin)
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserRolesUpdateRequestDTO(
        @NotNull(message = "La lista de roles es obligatoria")
        Set<String> roles
) {}