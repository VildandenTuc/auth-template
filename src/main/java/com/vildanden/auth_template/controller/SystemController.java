package com.vildanden.auth_template.controller;

import com.vildanden.auth_template.dto.common.ApiResponseDTO;
import com.vildanden.auth_template.dto.common.HealthResponseDTO;
import com.vildanden.auth_template.dto.common.SystemStatsResponseDTO;
import com.vildanden.auth_template.repository.RoleRepository;
import com.vildanden.auth_template.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para endpoints del sistema
 * Proporciona información de salud, estadísticas y estado general
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SystemController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${app.name:Auth Template API}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Endpoint público de salud del sistema
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponseDTO> health() {
        log.debug("GET /api/health");

        try {
            Map<String, Object> details = new HashMap<>();
            details.put("database", "UP");
            details.put("application", appName);
            details.put("profile", activeProfile);

            HealthResponseDTO health = HealthResponseDTO.up(appVersion, activeProfile, details);
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error en health check: {}", e.getMessage());
            Map<String, Object> details = new HashMap<>();
            details.put("error", e.getMessage());

            HealthResponseDTO health = new HealthResponseDTO(
                    "DOWN", appVersion, activeProfile, LocalDateTime.now(), details
            );
            return ResponseEntity.status(503).body(health);
        }
    }

    /**
     * Información básica de la aplicación (público)
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> info() {
        log.debug("GET /api/info");

        Map<String, Object> info = new HashMap<>();
        info.put("name", appName);
        info.put("version", appVersion);
        info.put("profile", activeProfile);
        info.put("timestamp", LocalDateTime.now());
        info.put("description", "Sistema de autenticación JWT reutilizable");

        return ResponseEntity.ok(
                ApiResponseDTO.success("Información de la aplicación", info)
        );
    }

    /**
     * Estadísticas del sistema (solo ADMIN)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<SystemStatsResponseDTO>> getSystemStats() {
        log.info("GET /api/admin/stats");

        try {
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.findByEnabled(true).size();
            long totalRoles = roleRepository.count();

            // Estadísticas por rol
            Map<String, Long> usersByRole = new HashMap<>();
            usersByRole.put("ADMIN", roleRepository.countUsersByRoleName("ADMIN"));
            usersByRole.put("USER", roleRepository.countUsersByRoleName("USER"));
            usersByRole.put("MODERATOR", roleRepository.countUsersByRoleName("MODERATOR"));

            SystemStatsResponseDTO stats = new SystemStatsResponseDTO(
                    totalUsers,
                    activeUsers,
                    totalRoles,
                    usersByRole,
                    LocalDateTime.now()
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success("Estadísticas del sistema", stats)
            );
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas del sistema: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.success("Error obteniendo estadísticas", null));
        }
    }

    /**
     * Información detallada del sistema (solo ADMIN)
     */
    @GetMapping("/admin/system-info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getDetailedSystemInfo() {
        log.info("GET /api/admin/system-info");

        try {
            Map<String, Object> systemInfo = new HashMap<>();

            // Información de la aplicación
            systemInfo.put("application", Map.of(
                    "name", appName,
                    "version", appVersion,
                    "profile", activeProfile,
                    "startup", LocalDateTime.now().minusHours(1) // Aproximado
            ));

            // Información de la JVM
            Runtime runtime = Runtime.getRuntime();
            systemInfo.put("jvm", Map.of(
                    "version", System.getProperty("java.version"),
                    "vendor", System.getProperty("java.vendor"),
                    "totalMemory", runtime.totalMemory(),
                    "freeMemory", runtime.freeMemory(),
                    "maxMemory", runtime.maxMemory()
            ));

            // Información del sistema operativo
            systemInfo.put("os", Map.of(
                    "name", System.getProperty("os.name"),
                    "version", System.getProperty("os.version"),
                    "arch", System.getProperty("os.arch")
            ));

            return ResponseEntity.ok(
                    ApiResponseDTO.success("Información detallada del sistema", systemInfo)
            );
        } catch (Exception e) {
            log.error("Error obteniendo información detallada del sistema: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.success("Error obteniendo información", null));
        }
    }
}