package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.application.service.auth.RefreshTokenValidationService;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private AuthenticationTokenService authenticationTokenService;

    @Mock
    private RefreshTokenValidationService refreshTokenValidationService;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    void executeShouldRotateTokensWhenRefreshTokenIsValid() {

        // Arrange
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("raw-refresh-token")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .build();

        User user = User.builder()
                .id(userId)
                .email("admin@econocom.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        TokenResponse expected = TokenResponse.builder()
                .accessToken("new-access")
                .refreshToken("new-refresh")
                .tokenType("Bearer")
                .expiresIn(900000L)
                .build();

        when(refreshTokenValidationService.validate("raw-refresh-token")).thenReturn(refreshToken);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authenticationTokenService.issue(user)).thenReturn(expected);

        // Act
        TokenResponse result = refreshTokenUseCase.execute(request);

        // Assert
        assertEquals("new-access", result.getAccessToken());
        assertEquals("new-refresh", result.getRefreshToken());

        verify(refreshTokenValidationService).validate("raw-refresh-token");
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).revokeById(tokenId);
        verify(authenticationTokenService).issue(user);
    }

    @Test
    void executeShouldThrowUserNotFoundWhenRefreshTokenOwnerDoesNotExist() {

        // Arrange
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("raw-refresh-token")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .build();

        when(refreshTokenValidationService.validate("raw-refresh-token")).thenReturn(refreshToken);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> refreshTokenUseCase.execute(request));

        verify(refreshTokenValidationService).validate("raw-refresh-token");
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository, never()).revokeById(tokenId);
        verify(authenticationTokenService, never()).issue(any(User.class));
    }
}


