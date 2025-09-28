package com.vildanden.auth_template.security;

import com.vildanden.auth_template.entity.User;
import com.vildanden.auth_template.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio personalizado para cargar detalles del usuario
 * Integra con Spring Security para autenticación
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por username o email
     * @param usernameOrEmail username o email del usuario
     * @return UserDetails para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Cargando usuario por username/email: {}", usernameOrEmail);

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", usernameOrEmail);
                    return new UsernameNotFoundException(
                            "Usuario no encontrado con username/email: " + usernameOrEmail
                    );
                });

        log.debug("Usuario encontrado: {} (ID: {})", user.getUsername(), user.getId());
        return UserPrincipal.create(user);
    }

    /**
     * Carga un usuario por ID
     * @param userId ID del usuario
     * @return UserDetails para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Cargando usuario por ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID: {}", userId);
                    return new UsernameNotFoundException("Usuario no encontrado con ID: " + userId);
                });

        log.debug("Usuario encontrado por ID: {} (username: {})", userId, user.getUsername());
        return UserPrincipal.create(user);
    }
}