package com.vildanden.auth_template.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitud de login
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record LoginRequestDTO(
        @NotBlank(message = "El username o email es obligatorio")
        @Size(min = 3, max = 100, message = "El username o email debe tener entre 3 y 100 caracteres")
        String usernameOrEmail,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
        String password
) {}