package com.vildanden.auth_template.controller;

import com.vildanden.auth_template.dto.auth.*;
import com.vildanden.auth_template.dto.common.ApiResponseDTO;
import com.vildanden.auth_template.security.UserPrincipal;
import com.vildanden.auth_template.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y gestión de tokens
 * Maneja endpoints públicos de login, registro y renovación de tokens
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para login de usuarios
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {

        log.info("POST /auth/login - Usuario: {}", loginRequest.usernameOrEmail());

        try {
            LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Login exitoso", response)
            );
        } catch (Exception e) {
            log.warn("Error en login para usuario: {} - {}", loginRequest.usernameOrEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.success("Credenciales inválidas", null));
        }
    }

    /**
     * Endpoint para registro de nuevos usuarios
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest) {

        log.info("POST /auth/register - Usuario: {}", registerRequest.username());

        try {
            RegisterResponseDTO response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Usuario registrado exitosamente", response));
        } catch (Exception e) {
            log.warn("Error en registro para usuario: {} - {}", registerRequest.username(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage(), null));
        }
    }

    /**
     * Endpoint para renovar access token usando refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshRequest) {

        log.info("POST /auth/refresh");

        try {
            LoginResponseDTO response = authService.refreshToken(refreshRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Token renovado exitosamente", response)
            );
        } catch (Exception e) {
            log.warn("Error renovando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.success("Token de renovación inválido", null));
        }
    }

    /**
     * Endpoint para cambiar contraseña del usuario autenticado
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("POST /auth/change-password - Usuario: {}", currentUser.getUsername());

        try {
            authService.changePassword(currentUser.getUsername(), changePasswordRequest);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Contraseña cambiada exitosamente")
            );
        } catch (Exception e) {
            log.warn("Error cambiando contraseña para usuario: {} - {}",
                    currentUser.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success(e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserSummaryDTO>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("GET /auth/me - Usuario: {}", currentUser.getUsername());

        try {
            // Crear UserSummaryDTO desde UserPrincipal
            UserSummaryDTO userSummary = new UserSummaryDTO(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    currentUser.getEmail(),
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getFullName(),
                    currentUser.getRoleNames().stream().collect(java.util.stream.Collectors.toSet())
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success("Información del usuario", userSummary)
            );
        } catch (Exception e) {
            log.warn("Error obteniendo información del usuario: {} - {}",
                    currentUser.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.success("Error obteniendo información del usuario", null));
        }
    }

    /**
     * Endpoint para validar un token
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponseDTO<Boolean>> validateToken(
            @RequestParam String token) {

        log.debug("POST /auth/validate");

        try {
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Token validado", isValid)
            );
        } catch (Exception e) {
            log.warn("Error validando token: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.success("Error validando token", false));
        }
    }

    /**
     * Endpoint de logout (invalidación del lado cliente)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("POST /auth/logout - Usuario: {}", currentUser.getUsername());

        // Con JWT stateless, el logout se maneja en el cliente eliminando el token
        // Aquí se puede implementar una blacklist de tokens si es necesario

        return ResponseEntity.ok(
                ApiResponseDTO.success("Logout exitoso")
        );
    }

}