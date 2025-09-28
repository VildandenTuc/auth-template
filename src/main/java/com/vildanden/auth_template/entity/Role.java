package com.vildanden.auth_template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los roles del sistema
 * Define los diferentes niveles de acceso y permisos
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre del rol no puede exceder 50 caracteres")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    /**
     * Constructor para crear roles con nombre
     * @param name nombre del rol
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Constructor para crear roles con nombre y descripción
     * @param name nombre del rol
     * @param description descripción del rol
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name != null && name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}