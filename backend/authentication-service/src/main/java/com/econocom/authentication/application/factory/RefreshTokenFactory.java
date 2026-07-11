package com.econocom.authentication.application.factory;

import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import com.econocom.authentication.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenFactory {

    private final Clock clock;

    private final SecurityProperties securityProperties;

    private final PasswordEncoderPort passwordEncoder;

    public RefreshToken create(UUID userId, String token) {

        LocalDateTime now = LocalDateTime.now(clock);

        return RefreshToken.builder()
                .userId(userId)
                .tokenHash(
                        passwordEncoder.encode(token)
                )
                .createdAt(now)
                .expiresAt(
                        now.plus(
                                Duration.ofMillis(
                                        securityProperties
                                                .getRefreshToken()
                                                .getExpiration()
                                )
                        )
                )
                .revoked(false)
                .build();

    }

}