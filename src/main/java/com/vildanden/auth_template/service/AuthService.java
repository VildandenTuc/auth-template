package com.vildanden.auth_template.service;

import com.vildanden.auth_template.dto.auth.*;
import com.vildanden.auth_template.entity.Role;
import com.vildanden.auth_template.entity.RoleName;
import com.vildanden.auth_template.entity.User;
import com.vildanden.auth_template.repository.RoleRepository;
import com.vildanden.auth_template.repository.UserRepository;
import com.vildanden.auth_template.security.JwtService;
import com.vildanden.auth_template.util.mapper.MapperDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vildanden.auth_template.config.JwtProperties;
import java.util.Set;

/**
 * Servicio de autenticación y gestión de usuarios
 * Maneja login, registro, refresh tokens y cambio de contraseñas
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MapperDTO mapperDTO;
    private final JwtProperties jwtProperties;

    /**
     * Autentica un usuario y genera tokens JWT
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.usernameOrEmail());

        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail(),
                        loginRequest.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        // Verificar que la cuenta esté disponible
        if (!user.isAccountAvailable()) {
            throw new RuntimeException("La cuenta de usuario no está disponible");
        }

        // Generar tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("Login exitoso para usuario: {} (ID: {})", user.getUsername(), user.getId());

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
               // jwtService.getExpirationInSeconds(),
                jwtProperties.getExpirationInSeconds(),
                mapperDTO.toUserSummaryDTO(user)
        );
    }

    /**
     * Registra un nuevo usuario en el sistema
     */
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        log.info("Intento de registro para usuario: {}", registerRequest.username());

        // Validar que no exista el usuario
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("El username ya está en uso: " + registerRequest.username());
        }

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("El email ya está en uso: " + registerRequest.email());
        }

        // Buscar rol USER por defecto
        Role userRole = roleRepository.findByName(RoleName.USER.getName())
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado en el sistema"));

        // Crear nuevo usuario
        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of(userRole))
                .build();

        user = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {} (ID: {})", user.getUsername(), user.getId());

        return new RegisterResponseDTO(
                "Usuario registrado exitosamente",
                mapperDTO.toUserSummaryDTO(user)
        );
    }

    /**
     * Renueva el access token usando un refresh token válido
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();

        log.debug("Renovando access token");

        // Validar que sea un refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token inválido para renovación");
        }

        // Validar el refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token expirado o inválido");
        }

        // Extraer usuario del token
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Verificar que la cuenta esté disponible
        if (!user.isAccountAvailable()) {
            throw new RuntimeException("La cuenta de usuario no está disponible");
        }

        // Generar nuevo access token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .toArray(String[]::new))
                .build();

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        log.debug("Access token renovado para usuario: {}", username);

        return new LoginResponseDTO(
                newAccessToken,
                refreshToken, // Mantener el mismo refresh token
                // jwtService.getExpirationInSeconds(),
                jwtProperties.getExpirationInSeconds(),
                mapperDTO.toUserSummaryDTO(user)
        );
    }

    /**
     * Cambia la contraseña del usuario autenticado
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDTO changePasswordRequest) {
        log.info("Cambio de contraseña solicitado para usuario: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Verificar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(changePasswordRequest.newPassword(), user.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);

        log.info("Contraseña cambiada exitosamente para usuario: {}", username);
    }

    /**
     * Valida si un token es válido
     */
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    /**
     * Obtiene información del usuario desde un token
     */
    @Transactional(readOnly = true)
    public UserSummaryDTO getUserFromToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return mapperDTO.toUserSummaryDTO(user);
    }
}