package com.vildanden.auth_template.repository;

import com.vildanden.auth_template.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de roles en la base de datos
 * Proporciona métodos para operaciones CRUD sobre roles
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre
     * @param name nombre del rol a buscar
     * @return Optional con el rol si existe
     */
    Optional<Role> findByName(String name);

    /**
     * Verifica si existe un rol con el nombre especificado
     * @param name nombre del rol a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Busca un rol por su nombre ignorando mayúsculas/minúsculas
     * @param name nombre del rol a buscar
     * @return Optional con el rol si existe
     */
    Optional<Role> findByNameIgnoreCase(String name);

    /**
     * Cuenta la cantidad de usuarios que tienen un rol específico
     * @param roleName nombre del rol
     * @return cantidad de usuarios con ese rol
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRoleName(String roleName);

    /**
     * Obtiene todos los roles ordenados por nombre
     * @return lista de roles ordenada por nombre
     */
    @Query("SELECT r FROM Role r ORDER BY r.name ASC")
    java.util.List<Role> findAllOrderByName();
}