package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.domain.exception.InvalidSsoStateException;
import com.econocom.authentication.domain.port.out.SsoStateStorePort;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SsoStateServiceTest {

    @Mock
    private SsoStateStorePort ssoStateStorePort;

    private SecurityProperties securityProperties;

    private SsoStateService ssoStateService;

    @BeforeEach
    void setUp() {

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        securityProperties = new SecurityProperties();
        securityProperties.getSso().setStateExpiration(Duration.ofMinutes(5));

        ssoStateService = new SsoStateService(
                ssoStateStorePort,
                fixedClock,
                securityProperties
        );
    }

    @Test
    void generateAndStoreShouldPersistStateAndReturnValue() {

        String state = ssoStateService.generateAndStore();

        assertNotNull(state);
        assertFalse(state.trim().isEmpty());

        ArgumentCaptor<Instant> expirationCaptor = ArgumentCaptor.forClass(Instant.class);

        verify(ssoStateStorePort).save(eq(state), expirationCaptor.capture());

        assertEquals(Instant.parse("2026-01-01T00:05:00Z"), expirationCaptor.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void consumeShouldThrowWhenStateIsBlank(String state) {

        assertThrows(
                InvalidSsoStateException.class,
                () -> ssoStateService.consume(state)
        );

        verifyNoInteractions(ssoStateStorePort);
    }

    @Test
    void consumeShouldThrowWhenStateDoesNotExist() {

        when(ssoStateStorePort.exists("invalid-state")).thenReturn(false);

        assertThrows(
                InvalidSsoStateException.class,
                () -> ssoStateService.consume("invalid-state")
        );

        verify(ssoStateStorePort, never()).remove("invalid-state");
    }

    @Test
    void consumeShouldRemoveStateWhenStateExists() {

        String validState = "valid-state";

        when(ssoStateStorePort.exists(validState)).thenReturn(true);

        ssoStateService.consume(validState);

        verify(ssoStateStorePort).exists(validState);
        verify(ssoStateStorePort).remove(validState);
    }

}

