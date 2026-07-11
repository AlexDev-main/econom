package com.econocom.authentication.infrastructure.external.sso;

import com.econocom.authentication.domain.port.out.SsoStateStorePort;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class InMemorySsoStateStoreAdapter implements SsoStateStorePort {

    private final ExpiringTemporaryStore stateStore;

    public InMemorySsoStateStoreAdapter(Clock clock) {
        this.stateStore = new ExpiringTemporaryStore(clock);
    }

    @Override
    public void save(String state, Instant expiresAt) {
        stateStore.save(state, expiresAt);
    }

    @Override
    public boolean exists(String state) {
        return stateStore.exists(state);
    }

    @Override
    public void remove(String state) {
        stateStore.remove(state);
    }

}

