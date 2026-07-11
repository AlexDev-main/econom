package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.domain.exception.InvalidSsoStateException;
import com.econocom.authentication.domain.port.out.SsoStateStorePort;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SsoStateService {

    private final SsoStateStorePort ssoStateStore;

    private final Clock clock;

    private final SecurityProperties securityProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAndStore() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String state = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        Instant expiresAt = Instant.now(clock)
                .plus(securityProperties.getSso().getStateExpiration());

        ssoStateStore.save(state, expiresAt);

        return state;
    }

    public void consume(String state) {

        if (state == null || state.trim().isEmpty()) {
            throw new InvalidSsoStateException();
        }

        if (!ssoStateStore.exists(state)) {
            throw new InvalidSsoStateException();
        }

        ssoStateStore.remove(state);
    }

}

