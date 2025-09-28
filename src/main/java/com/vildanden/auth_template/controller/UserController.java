package com.vildanden.auth_template.controller;

import com.vildanden.auth_template.dto.common.ApiResponseDTO;
import com.vildanden.auth_template.dto.common.PageResponseDTO;
import com.vildanden.auth_template.dto.user.*;
import com.vildanden.auth_template.security.UserPrincipal;
import com.vildanden.auth_template.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de usuarios
 * Proporciona endpoints para operaciones CRUD sobre usuarios
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getCurrentUserProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("GET /api/users/profile - Usuario: {}", currentUser.getUsername());

        try {
            UserResponseDTO user = userService.getUserByUsername(currentUser.getUsername());
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Perfil del usuario", user)
            );
        } catch (Exception e) {
            log.error("Error obteniendo perfil del usuario: {} - {}",
                    currentUser.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error obteniendo perfil", null));
        }
    }

    /**
     * Actualiza el perfil del usuario autenticado
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateCurrentUserProfile(
            @Valid @RequestBody UserUpdateRequestDTO updateRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("PUT /api/users/profile - Usuario: {}", currentUser.getUsername());

        try {
            UserResponseDTO updatedUser = userService.updateUser(currentUser.getId(), updateRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Perfil actualizado exitosamente", updatedUser)
            );
        } catch (Exception e) {
            log.error("Error actualizando perfil del usuario: {} - {}",
                    currentUser.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Obtiene todos los usuarios con paginación (solo ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("GET /api/users - página: {}, tamaño: {}", page, size);

        try {
            PageResponseDTO<UserResponseDTO> users = userService.getAllUsers(page, size, sortBy, sortDir);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Lista de usuarios", users)
            );
        } catch (Exception e) {
            log.error("Error obteniendo lista de usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error obteniendo usuarios", null));
        }
    }

    /**
     * Obtiene un usuario por ID (solo ADMIN)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id) {

        log.info("GET /api/users/{} ", id);

        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Usuario encontrado", user)
            );
        } catch (Exception e) {
            log.error("Error obteniendo usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo usuario (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
            @Valid @RequestBody UserCreateRequestDTO createRequest) {

        log.info("POST /api/users - Usuario: {}", createRequest.username());

        try {
            UserResponseDTO newUser = userService.createUser(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Usuario creado exitosamente", newUser));
        } catch (Exception e) {
            log.error("Error creando usuario: {} - {}", createRequest.username(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Actualiza un usuario existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO updateRequest) {

        log.info("PUT /api/users/{}", id);

        try {
            UserResponseDTO updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Usuario actualizado exitosamente", updatedUser)
            );
        } catch (Exception e) {
            log.error("Error actualizando usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Actualiza el estado de un usuario (solo ADMIN)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequestDTO statusRequest) {

        log.info("PUT /api/users/{}/status", id);

        try {
            UserResponseDTO updatedUser = userService.updateUserStatus(id, statusRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Estado del usuario actualizado", updatedUser)
            );
        } catch (Exception e) {
            log.error("Error actualizando estado del usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Actualiza los roles de un usuario (solo ADMIN)
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRolesUpdateRequestDTO rolesRequest) {

        log.info("PUT /api/users/{}/roles", id);

        try {
            UserResponseDTO updatedUser = userService.updateUserRoles(id, rolesRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Roles del usuario actualizados", updatedUser)
            );
        } catch (Exception e) {
            log.error("Error actualizando roles del usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Elimina un usuario (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {

        log.info("DELETE /api/users/{}", id);

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Usuario eliminado exitosamente")
            );
        } catch (Exception e) {
            log.error("Error eliminando usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage()));
        }
    }

    /**
     * Busca usuarios por filtros (solo ADMIN)
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> searchUsers(
            @RequestBody UserSearchFilterDTO searchFilter) {

        log.info("POST /api/users/search");

        try {
            List<UserResponseDTO> users = userService.searchUsers(searchFilter);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Resultados de búsqueda", users)
            );
        } catch (Exception e) {
            log.error("Error buscando usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error en búsqueda", null));
        }
    }
}