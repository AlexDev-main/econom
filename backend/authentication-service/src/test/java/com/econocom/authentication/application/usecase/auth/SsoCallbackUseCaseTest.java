package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthorizationCodeService;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.application.service.auth.SsoStateService;
import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.domain.model.SsoCallbackResult;
import com.econocom.authentication.domain.model.SsoProvider;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.SsoProviderPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SsoCallbackUseCaseTest {

    @Mock
    private SsoStateService ssoStateService;

    @Mock
    private SsoProviderPort ssoProviderPort;

    @Mock
    private AuthorizationCodeService authorizationCodeService;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private AuthenticationTokenService authenticationTokenService;

    @InjectMocks
    private SsoCallbackUseCase ssoCallbackUseCase;

    @Test
    void executeShouldIssueTokensForValidSsoCallback() {

        String code = "sim-code-abc";
        String state = "state-123";

        UUID userId = UUID.randomUUID();

        SsoCallbackResult callbackResult = SsoCallbackResult.builder()
                .email("test-admin@local")
                .provider(SsoProvider.SIMULATED)
                .providerUserId("abc")
                .build();

        User user = User.builder()
                .id(userId)
                .email("test-admin@local")
                .password("encoded")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        TokenResponse expected = TokenResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .tokenType("Bearer")
                .expiresIn(900000)
                .build();

        when(ssoProviderPort.validateCallback(code)).thenReturn(callbackResult);
        when(userRepositoryPort.findByEmail("test-admin@local")).thenReturn(Optional.of(user));
        when(authenticationTokenService.issue(user)).thenReturn(expected);

        TokenResponse result = ssoCallbackUseCase.execute(code, state);

        verify(ssoStateService).consume(state);
        verify(authorizationCodeService).consume(code);
        verify(refreshTokenRepositoryPort).revokeAllActiveByUser(userId);

        assertEquals(expected.getAccessToken(), result.getAccessToken());
        assertEquals(expected.getRefreshToken(), result.getRefreshToken());
    }

}

