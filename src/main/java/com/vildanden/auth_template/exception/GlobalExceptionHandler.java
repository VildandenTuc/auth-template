package com.vildanden.auth_template.exception;

import com.vildanden.auth_template.dto.common.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejo global de excepciones para toda la aplicación
 * Centraliza el tratamiento de errores y proporciona respuestas consistentes
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de campos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Errores de validación: {}", ex.getMessage());

        Map<String, List<String>> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            validationErrors.computeIfAbsent(fieldName, k ->
                    new java.util.ArrayList<>()).add(errorMessage);
        });

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Errores de validación en los datos enviados",
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                validationErrors
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja errores de autenticación (credenciales inválidas)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {

        log.warn("Credenciales inválidas: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Credenciales inválidas",
                "INVALID_CREDENTIALS",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja errores de usuario no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameNotFound(
            UsernameNotFoundException ex, WebRequest request) {

        log.warn("Usuario no encontrado: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Usuario no encontrado",
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja errores de acceso denegado (autorización)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Acceso denegado: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                "No tiene permisos para acceder a este recurso",
                "ACCESS_DENIED",
                HttpStatus.FORBIDDEN.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Maneja errores de runtime (lógica de negocio)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Error de runtime: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "BUSINESS_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja errores de argumentos ilegales
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Argumento ilegal: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja cualquier otra excepción no contemplada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Error inesperado: {}", ex.getMessage(), ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Ha ocurrido un error interno en el servidor",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}