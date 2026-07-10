package com.econocom.authentication.application.factory;

import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenFactory {

    private final Clock clock;

    private final SecurityProperties securityProperties;

    private final PasswordEncoderPort passwordEncoder;

    public RefreshToken create(User user, String tokenHash) {

        Instant now = Instant.now(clock);

        return RefreshToken.builder()
                .user(user)
                .tokenHash(
                        passwordEncoder.encode(tokenHash)
                )
                .createdAt(LocalDateTime.from(now))
                .expiresAt(
                        LocalDateTime.from(now.plusMillis(
                                securityProperties
                                        .getRefreshToken()
                                        .getExpiration()
                        ))
                )
                .revoked(false)
                .build();

    }

}