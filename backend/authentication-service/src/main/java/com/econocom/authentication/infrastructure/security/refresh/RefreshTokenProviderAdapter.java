package com.econocom.authentication.infrastructure.security.refresh;

import com.econocom.authentication.domain.port.out.RefreshTokenProviderPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenProviderAdapter implements RefreshTokenProviderPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String generate() {

        byte[] token = new byte[32];

        SECURE_RANDOM.nextBytes(token);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(token);

    }

}
