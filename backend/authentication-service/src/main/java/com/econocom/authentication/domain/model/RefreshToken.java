package com.econocom.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private UUID id;

    /**
     * BCrypt hash del Refresh Token.
     * Nunca almacenamos el token original.
     */
    private String tokenHash;

    private LocalDateTime expiresAt;

    private boolean revoked;

    private LocalDateTime createdAt;

    private User user;

}