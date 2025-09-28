package com.vildanden.auth_template.controller;

import com.vildanden.auth_template.dto.common.ApiResponseDTO;
import com.vildanden.auth_template.dto.role.*;
import com.vildanden.auth_template.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de roles
 * Proporciona endpoints para operaciones CRUD sobre roles
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Obtiene todos los roles (ADMIN y MODERATOR)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<List<RoleResponseDTO>>> getAllRoles() {

        log.info("GET /api/roles");

        try {
            List<RoleResponseDTO> roles = roleService.getAllRoles();
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Lista de roles", roles)
            );
        } catch (Exception e) {
            log.error("Error obteniendo lista de roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error obteniendo roles", null));
        }
    }

    /**
     * Obtiene roles simplificados para selects (ADMIN y MODERATOR)
     */
    @GetMapping("/simple")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<List<RoleSimpleDTO>>> getAllRolesSimple() {

        log.info("GET /api/roles/simple");

        try {
            List<RoleSimpleDTO> roles = roleService.getAllRolesSimple();
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Roles simplificados", roles)
            );
        } catch (Exception e) {
            log.error("Error obteniendo roles simplificados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error obteniendo roles", null));
        }
    }

    /**
     * Obtiene un rol por ID (ADMIN y MODERATOR)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> getRoleById(@PathVariable Long id) {

        log.info("GET /api/roles/{}", id);

        try {
            RoleResponseDTO role = roleService.getRoleById(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Rol encontrado", role)
            );
        } catch (Exception e) {
            log.error("Error obteniendo rol ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene un rol por nombre (ADMIN y MODERATOR)
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> getRoleByName(@PathVariable String name) {

        log.info("GET /api/roles/name/{}", name);

        try {
            RoleResponseDTO role = roleService.getRoleByName(name);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Rol encontrado", role)
            );
        } catch (Exception e) {
            log.error("Error obteniendo rol por nombre {}: {}", name, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo rol (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> createRole(
            @Valid @RequestBody RoleCreateRequestDTO createRequest) {

        log.info("POST /api/roles - Rol: {}", createRequest.name());

        try {
            RoleResponseDTO newRole = roleService.createRole(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Rol creado exitosamente", newRole));
        } catch (Exception e) {
            log.error("Error creando rol: {} - {}", createRequest.name(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Actualiza un rol existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequestDTO updateRequest) {

        log.info("PUT /api/roles/{}", id);

        try {
            RoleResponseDTO updatedRole = roleService.updateRole(id, updateRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Rol actualizado exitosamente", updatedRole)
            );
        } catch (Exception e) {
            log.error("Error actualizando rol ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Elimina un rol (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRole(@PathVariable Long id) {

        log.info("DELETE /api/roles/{}", id);

        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Rol eliminado exitosamente")
            );
        } catch (Exception e) {
            log.error("Error eliminando rol ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage()));
        }
    }

    /**
     * Verifica si un rol existe (ADMIN y MODERATOR)
     */
    @GetMapping("/exists/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<Boolean>> roleExists(@PathVariable String name) {

        log.debug("GET /api/roles/exists/{}", name);

        try {
            boolean exists = roleService.roleExists(name);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Verificación de existencia", exists)
            );
        } catch (Exception e) {
            log.error("Error verificando existencia del rol {}: {}", name, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error verificando rol", false));
        }
    }

    /**
     * Cuenta usuarios por rol (ADMIN y MODERATOR)
     */
    @GetMapping("/count-users/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<Long>> countUsersByRole(@PathVariable String name) {

        log.debug("GET /api/roles/count-users/{}", name);

        try {
            long count = roleService.countUsersByRole(name);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Conteo de usuarios", count)
            );
        } catch (Exception e) {
            log.error("Error contando usuarios del rol {}: {}", name, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error contando usuarios", 0L));
        }
    }
}