package com.vildanden.auth_template.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración para JWT
 * Mapea las propiedades del application.yml
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "MyVerySecureJWTSecretKeyForAuthTemplateApplication2025WithMinimum256BitsAndMoreCharactersToEnsureProperLength";
    private long expiration = 86400000;
    private long refreshExpiration = 604800000;

    public JwtProperties() {}

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * Obtiene el tiempo de expiración en segundos
     */
    public long getExpirationInSeconds() {
        return expiration / 1000;
    }

    /**
     * Obtiene el tiempo de expiración del refresh token en segundos
     */
    public long getRefreshExpirationInSeconds() {
        return refreshExpiration / 1000;
    }
}