package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.factory.RefreshTokenFactory;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationTokenServiceTest {

    @Mock
    private JwtProviderPort jwtProvider;

    @Mock
    private RefreshTokenProviderPort refreshTokenProvider;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private RefreshTokenFactory refreshTokenFactory;

    @InjectMocks
    private AuthenticationTokenService authenticationTokenService;

    @Test
    void issueShouldGenerateAndPersistTokens() {

        // Arrange
        UUID userId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("admin@econocom.com")
                .build();

        String refreshSecret = "refresh-secret";
        String accessToken = "access-token";

        RefreshToken refreshTokenToPersist = RefreshToken.builder()
                .userId(userId)
                .tokenHash("hash")
                .build();

        RefreshToken persistedRefreshToken = RefreshToken.builder()
                .id(refreshTokenId)
                .userId(userId)
                .tokenHash("hash")
                .build();

        when(jwtProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtProvider.getAccessTokenExpiration()).thenReturn(900000L);
        when(refreshTokenProvider.generate()).thenReturn(refreshSecret);
        when(refreshTokenFactory.create(userId, refreshSecret)).thenReturn(refreshTokenToPersist);
        when(refreshTokenRepository.save(refreshTokenToPersist)).thenReturn(persistedRefreshToken);

        // Act
        TokenResponse result = authenticationTokenService.issue(user);

        // Assert
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshTokenId + "." + refreshSecret, result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(900000L, result.getExpiresIn());

        verify(jwtProvider).generateAccessToken(user);
        verify(refreshTokenProvider).generate();
        verify(refreshTokenFactory).create(userId, refreshSecret);
        verify(refreshTokenRepository).save(refreshTokenToPersist);
        verify(jwtProvider).getAccessTokenExpiration();
    }
}

