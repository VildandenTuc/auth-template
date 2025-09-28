package com.vildanden.auth_template.dto.auth;

/**
 * DTO para respuesta de registro exitoso
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RegisterResponseDTO(
        String message,
        UserSummaryDTO user
) {}