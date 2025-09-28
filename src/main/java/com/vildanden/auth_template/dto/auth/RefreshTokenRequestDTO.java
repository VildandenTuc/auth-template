package com.vildanden.auth_template.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de refresh token
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RefreshTokenRequestDTO(
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}