package com.vildanden.auth_template.dto.auth;

/**
 * DTO resumen de usuario para responses de autenticación
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserSummaryDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        java.util.Set<String> roles
) {}