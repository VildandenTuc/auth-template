package com.vildanden.auth_template.dto.common;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para estadísticas del sistema
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record SystemStatsResponseDTO(
        long totalUsers,
        long activeUsers,
        long totalRoles,
        Map<String, Long> usersByRole,
        LocalDateTime lastUpdated
) {}