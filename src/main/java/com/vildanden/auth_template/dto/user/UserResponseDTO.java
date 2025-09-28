package com.vildanden.auth_template.dto.user;

import com.vildanden.auth_template.dto.role.RoleResponseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para respuesta completa de usuario
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        Boolean enabled,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Set<RoleResponseDTO> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}