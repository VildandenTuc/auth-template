package com.vildanden.auth_template.dto.role;

/**
 * DTO simplificado de rol (sin metadatos)
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record RoleSimpleDTO(
        Long id,
        String name,
        String description
) {}