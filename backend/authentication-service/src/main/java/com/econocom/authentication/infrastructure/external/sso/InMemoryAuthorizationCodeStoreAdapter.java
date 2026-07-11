package com.econocom.authentication.infrastructure.external.sso;

import com.econocom.authentication.domain.port.out.AuthorizationCodeStorePort;
import com.econocom.authentication.domain.exception.InvalidAuthorizationCodeException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class InMemoryAuthorizationCodeStoreAdapter implements AuthorizationCodeStorePort {

    private final ExpiringTemporaryStore codeStore;

    public InMemoryAuthorizationCodeStoreAdapter(Clock clock) {
        this.codeStore = new ExpiringTemporaryStore(clock);
    }

    @Override
    public void save(String code, Instant expiresAt) {
        codeStore.save(code, expiresAt);
    }

    @Override
    public void consume(String code) {
        codeStore.consume(code, new InvalidAuthorizationCodeException());
    }

}

