package com.vildanden.auth_template.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para interceptar y validar tokens JWT en las peticiones HTTP
 * Extrae el token del header Authorization y configura el SecurityContext
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = getTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
                String username = jwtService.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.validateToken(jwt, userDetails)) {
                        // Verificar que sea un access token
                        if (!jwtService.isAccessToken(jwt)) {
                            log.warn("Se intentó usar un refresh token como access token para usuario: {}", username);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("Usuario autenticado: {} con roles: {}",
                                username, userDetails.getAuthorities());
                    }
                }
            }
        } catch (JwtException e) {
            log.warn("Error procesando JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en filtro JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Determina si este filtro debe ser aplicado a la petición
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // No filtrar endpoints públicos - considerando context-path /api
        return path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.equals("/auth/refresh") ||
                path.equals("/auth/validate") ||
                path.equals("/health") ||
                path.equals("/info") ||
                path.startsWith("/actuator/");
    }
}