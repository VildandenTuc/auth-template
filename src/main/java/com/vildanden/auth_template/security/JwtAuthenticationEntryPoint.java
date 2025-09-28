package com.vildanden.auth_template.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vildanden.auth_template.dto.common.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point personalizado para manejar errores de autenticación
 * Responde con JSON en lugar de redirigir a login
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        log.warn("Unauthorized access attempt to: {} - {}",
                request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "Acceso no autorizado",
                "Debe autenticarse para acceder a este recurso",
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}