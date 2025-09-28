package com.vildanden.auth_template.dto.common;

import java.util.List;

/**
 * DTO para respuestas paginadas
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {}