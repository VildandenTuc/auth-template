package com.vildanden.auth_template.service;

import com.vildanden.auth_template.config.JwtProperties;
import com.vildanden.auth_template.dto.auth.LoginRequestDTO;
import com.vildanden.auth_template.dto.auth.LoginResponseDTO;
import com.vildanden.auth_template.dto.auth.RegisterRequestDTO;
import com.vildanden.auth_template.dto.auth.RegisterResponseDTO;
import com.vildanden.auth_template.entity.Role;
import com.vildanden.auth_template.entity.RoleName;
import com.vildanden.auth_template.entity.User;
import com.vildanden.auth_template.repository.RoleRepository;
import com.vildanden.auth_template.repository.UserRepository;
import com.vildanden.auth_template.security.JwtService;
import com.vildanden.auth_template.util.mapper.MapperDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private MapperDTO mapperDTO;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = Role.builder()
                .name("USER")
                .description("Usuario estándar")
                .build();
        userRole.setId(1L); // Usar setter para el ID

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of(userRole))
                .build();
        testUser.setId(1L); // Usar setter para el ID
    }

    @Test
    void login_SuccessfulAuthentication_ReturnsLoginResponse() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "password123");
        Authentication authentication = mock(Authentication.class);
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", java.util.Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(jwtProperties.getExpirationInSeconds()).thenReturn(3600L);
        when(mapperDTO.toUserSummaryDTO(testUser)).thenReturn(null); // Simplificado para test

        // When
        LoginResponseDTO result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(3600L, result.expiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(jwtService).generateAccessToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
        verify(jwtProperties).getExpirationInSeconds();
    }

    @Test
    void login_UserNotFoundAfterAuthentication_ThrowsException() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "password123");
        Authentication authentication = mock(Authentication.class);
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", java.util.Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("Usuario no encontrado después de autenticación", exception.getMessage());
    }

    @Test
    void register_ValidRequest_ReturnsRegisterResponse() {
        // Given
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "newuser", "new@example.com", "password123", "New", "User");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER.getName()))
                .thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(mapperDTO.toUserSummaryDTO(testUser)).thenReturn(null); // Simplificado para test

        // When
        RegisterResponseDTO result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals("Usuario registrado exitosamente", result.message());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(roleRepository).findByName(RoleName.USER.getName());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        // Given
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "existinguser", "new@example.com", "password123", "New", "User");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("El username ya está en uso: existinguser", exception.getMessage());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Given
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "newuser", "existing@example.com", "password123", "New", "User");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("El email ya está en uso: existing@example.com", exception.getMessage());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = "valid-token";
        when(jwtService.validateToken(token)).thenReturn(true);

        // When
        boolean result = authService.validateToken(token);

        // Then
        assertTrue(result);
        verify(jwtService).validateToken(token);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String token = "invalid-token";
        when(jwtService.validateToken(token)).thenReturn(false);

        // When
        boolean result = authService.validateToken(token);

        // Then
        assertFalse(result);
        verify(jwtService).validateToken(token);
    }
}