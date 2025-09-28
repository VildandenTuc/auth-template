package com.vildanden.auth_template.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
**
        * DTO para cambio de contraseña
 *
         * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record ChangePasswordRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, max = 50, message = "La nueva contraseña debe tener entre 6 y 50 caracteres")
        String newPassword
) {}