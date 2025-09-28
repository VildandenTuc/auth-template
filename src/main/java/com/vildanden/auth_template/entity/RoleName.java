package com.vildanden.auth_template.entity;

/**
 * Enumeración que define los roles estándar del sistema
 * Facilita el manejo de roles de manera type-safe
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public enum RoleName {

    /**
     * Administrador del sistema con acceso completo
     */
    ADMIN("ADMIN", "Administrador del sistema con acceso completo"),

    /**
     * Usuario estándar con permisos básicos
     */
    USER("USER", "Usuario estándar con permisos básicos"),

    /**
     * Moderador con permisos intermedios
     */
    MODERATOR("MODERATOR", "Moderador con permisos intermedios");

    private final String name;
    private final String description;

    RoleName(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Convierte el enum a string para usar en consultas
     * @return nombre del rol como string
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Obtiene un RoleName a partir de un string
     * @param name nombre del rol
     * @return RoleName correspondiente
     * @throws IllegalArgumentException si el rol no existe
     */
    public static RoleName fromString(String name) {
        for (RoleName roleName : RoleName.values()) {
            if (roleName.name.equalsIgnoreCase(name)) {
                return roleName;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + name);
    }

    /**
     * Verifica si un string corresponde a un rol válido
     * @param name nombre del rol a verificar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValid(String name) {
        try {
            fromString(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}