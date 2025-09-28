package com.vildanden.auth_template.dto.common;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para información de salud del sistema
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record HealthResponseDTO(
        String status,
        String version,
        String environment,
        LocalDateTime timestamp,
        Map<String, Object> details
) {
    public static HealthResponseDTO up(String version, String environment) {
        return new HealthResponseDTO("UP", version, environment, LocalDateTime.now(), null);
    }

    public static HealthResponseDTO up(String version, String environment, Map<String, Object> details) {
        return new HealthResponseDTO("UP", version, environment, LocalDateTime.now(), details);
    }
}