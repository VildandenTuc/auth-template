package com.vildanden.auth_template.dto.auth;

/**
 * DTO para respuesta de login exitoso
 *
 * @author Guido Alfredo Albarracín
 * @version 1.0.0
 */
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserSummaryDTO user
) {
    public LoginResponseDTO(String accessToken,
                            String refreshToken,
                            Long expiresIn,
                            UserSummaryDTO user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
