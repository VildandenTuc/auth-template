package com.vildanden.auth_template;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {

    @Test
    public void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);

        // Verificar que el hash funciona
        boolean matches = encoder.matches(password, hash);
        System.out.println("Matches: " + matches);

        // Probar con el hash existente
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVklkC";
        boolean existingMatches = encoder.matches(password, existingHash);
        System.out.println("Existing hash matches: " + existingMatches);
    }
}