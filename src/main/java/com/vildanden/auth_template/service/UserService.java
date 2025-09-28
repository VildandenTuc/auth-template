package com.vildanden.auth_template.service;

import com.vildanden.auth_template.dto.common.PageResponseDTO;
import com.vildanden.auth_template.dto.user.*;
import com.vildanden.auth_template.entity.Role;
import com.vildanden.auth_template.entity.User;
import com.vildanden.auth_template.repository.RoleRepository;
import com.vildanden.auth_template.repository.UserRepository;
import com.vildanden.auth_template.util.mapper.MapperDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de usuarios
 * Proporciona operaciones CRUD y búsquedas de usuarios
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperDTO mapperDTO;

    /**
     * Obtiene todos los usuarios con paginación
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<UserResponseDTO> getAllUsers(int page, int size, String sortBy, String sortDir) {
        log.debug("Obteniendo usuarios - página: {}, tamaño: {}, orden: {} {}", page, size, sortBy, sortDir);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDTO> users = userPage.getContent().stream()
                .map(mapperDTO::toUserResponseDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.isEmpty()
        );
    }

    /**
     * Busca un usuario por ID
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.debug("Buscando usuario por ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Busca un usuario por username
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        log.debug("Buscando usuario por username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO createRequest) {
        log.info("Creando nuevo usuario: {}", createRequest.username());

        // Validar que no exista el usuario
        if (userRepository.existsByUsername(createRequest.username())) {
            throw new RuntimeException("El username ya está en uso: " + createRequest.username());
        }

        if (userRepository.existsByEmail(createRequest.email())) {
            throw new RuntimeException("El email ya está en uso: " + createRequest.email());
        }

        // Obtener roles
        Set<Role> roles = getRolesByNames(createRequest.roles());

        // Crear usuario
        User user = User.builder()
                .username(createRequest.username())
                .email(createRequest.email())
                .password(passwordEncoder.encode(createRequest.password()))
                .firstName(createRequest.firstName())
                .lastName(createRequest.lastName())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(roles)
                .build();

        user = userRepository.save(user);
        log.info("Usuario creado exitosamente: {} (ID: {})", user.getUsername(), user.getId());

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateRequest) {
        log.info("Actualizando usuario ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Verificar que el email no esté en uso por otro usuario
        if (!user.getEmail().equals(updateRequest.email()) &&
                userRepository.existsByEmail(updateRequest.email())) {
            throw new RuntimeException("El email ya está en uso: " + updateRequest.email());
        }

        // Actualizar campos
        user.setEmail(updateRequest.email());
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());

        user = userRepository.save(user);
        log.info("Usuario actualizado exitosamente: {} (ID: {})", user.getUsername(), user.getId());

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Actualiza el estado de un usuario (solo admin)
     */
    @Transactional
    public UserResponseDTO updateUserStatus(Long id, UserStatusUpdateRequestDTO statusRequest) {
        log.info("Actualizando estado del usuario ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        user.setEnabled(statusRequest.enabled());
        user.setAccountNonExpired(statusRequest.accountNonExpired());
        user.setAccountNonLocked(statusRequest.accountNonLocked());
        user.setCredentialsNonExpired(statusRequest.credentialsNonExpired());

        user = userRepository.save(user);
        log.info("Estado del usuario actualizado: {} (ID: {})", user.getUsername(), user.getId());

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Actualiza los roles de un usuario (solo admin)
     */
    @Transactional
    public UserResponseDTO updateUserRoles(Long id, UserRolesUpdateRequestDTO rolesRequest) {
        log.info("Actualizando roles del usuario ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        Set<Role> roles = getRolesByNames(rolesRequest.roles());
        user.setRoles(roles);

        user = userRepository.save(user);
        log.info("Roles del usuario actualizados: {} (ID: {}) - Roles: {}",
                user.getUsername(), user.getId(), rolesRequest.roles());

        return mapperDTO.toUserResponseDTO(user);
    }

    /**
     * Elimina un usuario
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Eliminando usuario ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("Usuario eliminado exitosamente ID: {}", id);
    }

    /**
     * Busca usuarios por filtros
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(UserSearchFilterDTO filter) {
        log.debug("Buscando usuarios con filtros: {}", filter);

        // Implementación básica - se puede extender con Specifications
        List<User> users;

        if (filter.roleName() != null && !filter.roleName().isBlank()) {
            users = userRepository.findByRoleName(filter.roleName());
        } else if (filter.enabled() != null) {
            users = userRepository.findByEnabled(filter.enabled());
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(mapperDTO::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de usuarios
     */
    @Transactional(readOnly = true)
    public UserPageResponseDTO getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByEnabled(true).size();

        log.debug("Estadísticas de usuarios - Total: {}, Activos: {}", totalUsers, activeUsers);

        // Retornar datos básicos (se puede extender)
        return new UserPageResponseDTO(
                List.of(), 0, 0, totalUsers, 0, true, true
        );
    }

    /**
     * Obtiene roles por nombres
     */
    private Set<Role> getRolesByNames(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            // Rol por defecto
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
            return Set.of(defaultRole);
        }

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        if (roles.isEmpty()) {
            throw new RuntimeException("No se pudieron obtener los roles especificados");
        }

        return roles;
    }
}