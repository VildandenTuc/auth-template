package com.vildanden.auth_template;

import com.vildanden.auth_template.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicación Auth Template
 * Sistema de autenticación JWT reutilizable para proyectos Spring Boot
 *
 * @author Guido Alfredo Albarracin
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties.class)
public class AuthTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthTemplateApplication.class, args);
    }
}