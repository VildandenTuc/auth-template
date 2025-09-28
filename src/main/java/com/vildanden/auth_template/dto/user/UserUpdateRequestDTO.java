package com.vildanden.auth_template.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar usuario existente
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserUpdateRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 100, message = "El email no puede exceder 100 caracteres")
        String email,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
        String lastName
) {}