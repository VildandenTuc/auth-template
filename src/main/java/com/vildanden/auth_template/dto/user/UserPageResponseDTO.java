package com.vildanden.auth_template.dto.user;

/**
 * DTO para respuesta de lista paginada de usuarios
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserPageResponseDTO(
        java.util.List<UserResponseDTO> users,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {}