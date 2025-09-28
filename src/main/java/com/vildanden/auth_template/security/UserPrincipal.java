package com.vildanden.auth_template.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vildanden.auth_template.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetails para Spring Security
 * Adapta la entidad User a los requerimientos de Spring Security
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method para crear UserPrincipal desde User entity
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired != null && accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked != null && accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired != null && credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }

    /**
     * Verifica si el usuario tiene alguno de los roles especificados
     */
    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene los nombres de los roles (sin prefijo ROLE_)
     */
    public List<String> getRoleNames() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}