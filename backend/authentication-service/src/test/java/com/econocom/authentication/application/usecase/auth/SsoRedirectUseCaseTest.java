package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.service.auth.SsoStateService;
import com.econocom.authentication.domain.port.out.SsoProviderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SsoRedirectUseCaseTest {

    @Mock
    private SsoStateService ssoStateService;

    @Mock
    private SsoProviderPort ssoProvider;

    @InjectMocks
    private SsoRedirectUseCase ssoRedirectUseCase;

    @Test
    void executeShouldReturnProviderAuthorizationUrlWithStoredState() {

        // Arrange
        String state = "state-123";
        String authorizationUrl = "https://sso.example.com/authorize?state=state-123";

        when(ssoStateService.generateAndStore()).thenReturn(state);
        when(ssoProvider.buildAuthorizationUrl(state)).thenReturn(authorizationUrl);

        // Act
        URI result = ssoRedirectUseCase.execute();

        // Assert
        assertEquals(authorizationUrl, result.toString());
        verify(ssoStateService).generateAndStore();
        verify(ssoProvider).buildAuthorizationUrl(state);
    }
}

