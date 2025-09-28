package com.vildanden.auth_template.dto.user;

/**
 * DTO para filtros de búsqueda de usuarios
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserSearchFilterDTO(
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String roleName
) {}