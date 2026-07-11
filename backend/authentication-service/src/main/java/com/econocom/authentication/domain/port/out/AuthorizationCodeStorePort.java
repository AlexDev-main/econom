package com.econocom.authentication.domain.port.out;

import java.time.Instant;

public interface AuthorizationCodeStorePort {

    void save(String code, Instant expiresAt);

    void consume(String code);

}

