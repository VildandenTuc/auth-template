package com.vildanden.auth_template.util.mapper;

import com.vildanden.auth_template.dto.auth.UserSummaryDTO;
import com.vildanden.auth_template.dto.role.RoleResponseDTO;
import com.vildanden.auth_template.dto.role.RoleSimpleDTO;
import com.vildanden.auth_template.dto.user.UserResponseDTO;
import com.vildanden.auth_template.entity.Role;
import com.vildanden.auth_template.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mappers para conversión entre entidades y DTOs
 * Centraliza la lógica de transformación de datos
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Component
public class MapperDTO {

    /**
     * Convierte User entity a UserResponseDTO
     */
    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getRoles().stream()
                        .map(this::toRoleResponseDTO)
                        .collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Convierte User entity a UserSummaryDTO (para auth responses)
     */
    public UserSummaryDTO toUserSummaryDTO(User user) {
        if (user == null) return null;

        return new UserSummaryDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Convierte Role entity a RoleResponseDTO
     */
    public RoleResponseDTO toRoleResponseDTO(Role role) {
        if (role == null) return null;

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getUsers() != null ? role.getUsers().size() : 0,
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    /**
     * Convierte Role entity a RoleSimpleDTO
     */
    public RoleSimpleDTO toRoleSimpleDTO(Role role) {
        if (role == null) return null;

        return new RoleSimpleDTO(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }

    /**
     * Convierte Set de User entities a Set de UserResponseDTO
     */
    public Set<UserResponseDTO> toUserResponseDTOSet(Set<User> users) {
        if (users == null) return Set.of();

        return users.stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Convierte Set de Role entities a Set de RoleResponseDTO
     */
    public Set<RoleResponseDTO> toRoleResponseDTOSet(Set<Role> roles) {
        if (roles == null) return Set.of();

        return roles.stream()
                .map(this::toRoleResponseDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Convierte Set de Role entities a Set de RoleSimpleDTO
     */
    public Set<RoleSimpleDTO> toRoleSimpleDTOSet(Set<Role> roles) {
        if (roles == null) return Set.of();

        return roles.stream()
                .map(this::toRoleSimpleDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Convierte Set de Role entities a Set de nombres de roles (String)
     */
    public Set<String> toRoleNameSet(Set<Role> roles) {
        if (roles == null) return Set.of();

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}