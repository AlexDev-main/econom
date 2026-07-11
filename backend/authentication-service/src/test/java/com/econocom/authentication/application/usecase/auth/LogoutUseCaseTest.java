package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.service.auth.RefreshTokenValidationService;
import com.econocom.authentication.domain.exception.InvalidRefreshTokenException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private RefreshTokenValidationService refreshTokenValidationService;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Test
    void executeShouldRevokeTokenWhenRefreshTokenIsValid() {

        // Arrange
        UUID tokenId = UUID.randomUUID();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .build();

        when(refreshTokenValidationService.validate("valid-refresh-token")).thenReturn(refreshToken);

        // Act
        logoutUseCase.execute(request);

        // Assert
        verify(refreshTokenValidationService).validate("valid-refresh-token");
        verify(refreshTokenRepository).revokeById(tokenId);
    }

    @Test
    void executeShouldNotRevokeTokenWhenValidationFails() {

        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid-token")
                .build();

        when(refreshTokenValidationService.validate("invalid-token"))
                .thenThrow(new InvalidRefreshTokenException());

        // Act + Assert
        assertThrows(InvalidRefreshTokenException.class, () -> logoutUseCase.execute(request));

        verifyNoInteractions(refreshTokenRepository);
    }
}


