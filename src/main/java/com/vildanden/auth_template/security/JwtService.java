package com.vildanden.auth_template.security;

import com.vildanden.auth_template.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la gestión de tokens JWT
 * Genera, valida y extrae información de los tokens
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Genera la clave secreta para firmar los tokens
     */
    private SecretKey getSigningKey() {
        String secret = jwtProperties.getSecret();

        // Log para debug
        log.debug("JWT Secret length: {}", secret != null ? secret.length() : "null");

        // Asegurar que la clave tenga al menos 256 bits (32 bytes)
        if (secret == null || secret.length() < 32) {
            // Expandir la clave si es muy corta
            secret = (secret != null ? secret : "") + "0".repeat(Math.max(0, 32 - (secret != null ? secret.length() : 0)));
        }

        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Usar siempre la clave configurada (nunca generar una aleatoria)
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el username del token JWT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Token JWT no soportado: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformado: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            log.warn("Token JWT con firma inválida: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Token JWT vacío: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si el token ha expirado
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un access token para el usuario
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(claims, userDetails.getUsername(), jwtProperties.getExpiration());
    }

    /**
     * Genera un refresh token para el usuario
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), jwtProperties.getRefreshExpiration());
    }

    /**
     * Genera un token con claims personalizados
     */
    public String generateTokenWithClaims(Map<String, Object> extraClaims, UserDetails userDetails) {
        return createToken(extraClaims, userDetails.getUsername(), jwtProperties.getExpiration());
    }

    /**
     * Crea un token JWT
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si el token es válido para el usuario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Error validando token para usuario {}: {}",
                    userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Valida un token sin usuario específico
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el tipo de token (access/refresh)
     */
    public String getTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    /**
     * Verifica si es un access token
     */
    public Boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    /**
     * Verifica si es un refresh token
     */
    public Boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Obtiene el tiempo restante del token en segundos
     */
    public Long getTimeToExpiration(String token) {
        Date expiration = extractExpiration(token);
        Date now = new Date();
        return Math.max(0, (expiration.getTime() - now.getTime()) / 1000);
    }
}