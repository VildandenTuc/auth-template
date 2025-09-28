package com.vildanden.auth_template.service;

import com.vildanden.auth_template.dto.role.*;
import com.vildanden.auth_template.entity.Role;
import com.vildanden.auth_template.repository.RoleRepository;
import com.vildanden.auth_template.util.mapper.MapperDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de roles
 * Proporciona operaciones CRUD sobre roles del sistema
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MapperDTO mapperDTO;

    /**
     * Obtiene todos los roles del sistema
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        log.debug("Obteniendo todos los roles");

        return roleRepository.findAllOrderByName().stream()
                .map(mapperDTO::toRoleResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un rol por ID
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(Long id) {
        log.debug("Buscando rol por ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        return mapperDTO.toRoleResponseDTO(role);
    }

    /**
     * Obtiene un rol por nombre
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByName(String name) {
        log.debug("Buscando rol por nombre: {}", name);

        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));

        return mapperDTO.toRoleResponseDTO(role);
    }

    /**
     * Crea un nuevo rol
     */
    @Transactional
    public RoleResponseDTO createRole(RoleCreateRequestDTO createRequest) {
        log.info("Creando nuevo rol: {}", createRequest.name());

        // Validar que no exista el rol
        if (roleRepository.existsByName(createRequest.name())) {
            throw new RuntimeException("El rol ya existe: " + createRequest.name());
        }

        Role role = Role.builder()
                .name(createRequest.name())
                .description(createRequest.description())
                .build();

        role = roleRepository.save(role);
        log.info("Rol creado exitosamente: {} (ID: {})", role.getName(), role.getId());

        return mapperDTO.toRoleResponseDTO(role);
    }

    /**
     * Actualiza un rol existente
     */
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleUpdateRequestDTO updateRequest) {
        log.info("Actualizando rol ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        // Solo actualizar la descripción (el nombre no se puede cambiar)
        role.setDescription(updateRequest.description());

        role = roleRepository.save(role);
        log.info("Rol actualizado exitosamente: {} (ID: {})", role.getName(), role.getId());

        return mapperDTO.toRoleResponseDTO(role);
    }

    /**
     * Elimina un rol
     */
    @Transactional
    public void deleteRole(Long id) {
        log.info("Eliminando rol ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        // Verificar que el rol no esté en uso
        long userCount = roleRepository.countUsersByRoleName(role.getName());
        if (userCount > 0) {
            throw new RuntimeException("No se puede eliminar el rol porque tiene " + userCount + " usuarios asignados");
        }

        // No permitir eliminar roles del sistema básicos
        if ("ADMIN".equals(role.getName()) || "USER".equals(role.getName())) {
            throw new RuntimeException("No se puede eliminar el rol del sistema: " + role.getName());
        }

        roleRepository.deleteById(id);
        log.info("Rol eliminado exitosamente: {} (ID: {})", role.getName(), id);
    }

    /**
     * Obtiene roles simplificados (para selects)
     */
    @Transactional(readOnly = true)
    public List<RoleSimpleDTO> getAllRolesSimple() {
        log.debug("Obteniendo roles simplificados");

        return roleRepository.findAllOrderByName().stream()
                .map(mapperDTO::toRoleSimpleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un rol existe
     */
    @Transactional(readOnly = true)
    public boolean roleExists(String name) {
        return roleRepository.existsByName(name);
    }

    /**
     * Cuenta usuarios por rol
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(String roleName) {
        return roleRepository.countUsersByRoleName(roleName);
    }
}