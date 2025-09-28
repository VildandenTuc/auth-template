package com.vildanden.auth_template.repository;

import com.vildanden.auth_template.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios en la base de datos
 * Proporciona métodos para operaciones CRUD sobre usuarios
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     * @param username nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email
     * @param email email del usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por username o email
     * @param username nombre de usuario
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Verifica si existe un usuario con el username especificado
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email especificado
     * @param email email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios habilitados
     * @param enabled estado de habilitación
     * @return lista de usuarios habilitados
     */
    List<User> findByEnabled(Boolean enabled);

    /**
     * Busca usuarios por rol
     * @param roleName nombre del rol
     * @return lista de usuarios que tienen el rol especificado
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial)
     * @param firstName nombre a buscar
     * @param lastName apellido a buscar
     * @return lista de usuarios que coinciden
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> findByFirstNameContainingOrLastNameContaining(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName);

    /**
     * Busca usuarios con cuentas disponibles (enabled=true, non-expired, non-locked)
     * @return lista de usuarios con cuentas disponibles
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.accountNonExpired = true " +
            "AND u.accountNonLocked = true AND u.credentialsNonExpired = true")
    List<User> findAvailableUsers();

    /**
     * Cuenta usuarios por rol
     * @param roleName nombre del rol
     * @return cantidad de usuarios con ese rol
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    /**
     * Obtiene usuarios con sus roles (optimización para evitar N+1)
     * @return lista de usuarios con roles cargados
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();
}