package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.domain.port.out.AuthorizationCodeStorePort;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthorizationCodeService {

    private final AuthorizationCodeStorePort authorizationCodeStore;

    private final SecurityProperties securityProperties;

    private final Clock clock;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAndStore() {

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String code = securityProperties.getSso().getSimulated().getCodePrefix()
                + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        Instant expiresAt = Instant.now(clock)
                .plus(securityProperties.getSso().getAuthorizationCodeExpiration());

        authorizationCodeStore.save(code, expiresAt);

        return code;
    }

    public void consume(String code) {
        authorizationCodeStore.consume(code);
    }

}

