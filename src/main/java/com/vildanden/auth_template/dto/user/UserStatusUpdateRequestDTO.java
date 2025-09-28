package com.vildanden.auth_template.dto.user;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar estado de usuario (solo admin)
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserStatusUpdateRequestDTO(
        @NotNull(message = "El estado enabled es obligatorio")
        Boolean enabled,

        @NotNull(message = "El estado accountNonExpired es obligatorio")
        Boolean accountNonExpired,

        @NotNull(message = "El estado accountNonLocked es obligatorio")
        Boolean accountNonLocked,

        @NotNull(message = "El estado credentialsNonExpired es obligatorio")
        Boolean credentialsNonExpired
) {}