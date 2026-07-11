package com.econocom.authentication.domain.port.out;

import java.time.Instant;

public interface SsoStateStorePort {

    void save(String state, Instant expiresAt);

    boolean exists(String state);

    void remove(String state);

}

