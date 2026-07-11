package com.econocom.authentication.infrastructure.external.sso;

import com.econocom.authentication.domain.exception.BusinessException;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExpiringTemporaryStore {

    private final ConcurrentMap<String, Instant> values = new ConcurrentHashMap<>();

    private final Clock clock;

    public ExpiringTemporaryStore(Clock clock) {
        this.clock = clock;
    }

    public void save(String key, Instant expiresAt) {
        values.put(key, expiresAt);
    }

    public boolean exists(String key) {

        Instant expiresAt = values.get(key);

        if (expiresAt == null) {
            return false;
        }

        if (Instant.now(clock).isAfter(expiresAt)) {
            values.remove(key);
            return false;
        }

        return true;
    }

    public void remove(String key) {
        values.remove(key);
    }

    public void consume(String key, BusinessException exception) {

        if (!exists(key)) {
            throw exception;
        }

        remove(key);
    }

}

