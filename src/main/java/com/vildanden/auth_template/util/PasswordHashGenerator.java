package com.vildanden.auth_template.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";

        // Generar nuevo hash
        String newHash = encoder.encode(password);
        System.out.println("=== NUEVO HASH GENERADO ===");
        System.out.println("Password: " + password);
        System.out.println("Nuevo Hash: " + newHash);
        System.out.println("Matches: " + encoder.matches(password, newHash));

        // Probar hashes existentes
        String hashV2 = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVklkC";
        String hashV3 = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";

        System.out.println("\n=== VERIFICACION DE HASHES EXISTENTES ===");
        System.out.println("Hash V2 matches: " + encoder.matches(password, hashV2));
        System.out.println("Hash V3 matches: " + encoder.matches(password, hashV3));

        System.out.println("\n=== SQL PARA ACTUALIZAR ===");
        System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'admin';");
    }
}