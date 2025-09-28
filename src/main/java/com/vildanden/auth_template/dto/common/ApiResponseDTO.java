package com.vildanden.auth_template.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO para respuestas exitosas de la API
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseDTO<T>(
        boolean success,
        String message,
        T data,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp
) {
    public ApiResponseDTO(boolean success, String message, T data) {
        this(success, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Operación exitosa", data);
    }

    public static ApiResponseDTO<Void> success(String message) {
        return new ApiResponseDTO<>(true, message, null);
    }
}