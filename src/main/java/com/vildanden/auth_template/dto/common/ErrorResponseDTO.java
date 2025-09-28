package com.vildanden.auth_template.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para respuestas de error de la API
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        boolean success,
        String message,
        String error,
        int status,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        Map<String, List<String>> validationErrors
) {
    public ErrorResponseDTO(String message, String error, int status, String path) {
        this(false, message, error, status, path, LocalDateTime.now(), null);
    }

    public ErrorResponseDTO(String message, String error, int status, String path, Map<String, List<String>> validationErrors) {
        this(false, message, error, status, path, LocalDateTime.now(), validationErrors);
    }
}