package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.domain.exception.InvalidRefreshTokenException;
import com.econocom.authentication.domain.exception.RefreshTokenExpiredException;
import com.econocom.authentication.domain.exception.RefreshTokenNotFoundException;
import com.econocom.authentication.domain.exception.RefreshTokenRevokedException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenValidationServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private RefreshTokenValidationService refreshTokenValidationService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
        refreshTokenValidationService = new RefreshTokenValidationService(
                refreshTokenRepository,
                passwordEncoder,
                fixedClock
        );
    }

    @Test
    void validateShouldReturnRefreshTokenWhenTokenIsValid() {

        // Arrange
        UUID tokenId = UUID.randomUUID();
        String secret = "refresh-secret";

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .tokenHash("token-hash")
                .revoked(false)
                .expiresAt(LocalDateTime.of(2026, 1, 1, 10, 1, 0))
                .build();

        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(refreshToken));
        when(passwordEncoder.matches(secret, "token-hash")).thenReturn(true);

        // Act
        RefreshToken result = refreshTokenValidationService.validate(tokenId + "." + secret);

        // Assert
        assertSame(refreshToken, result);
        verify(refreshTokenRepository).findById(tokenId);
        verify(passwordEncoder).matches(secret, "token-hash");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc", ".secret", "1234.", "not-a-uuid.secret"})
    void validateShouldThrowInvalidRefreshTokenForMalformedValues(String rawRefreshToken) {

        // Arrange + Act + Assert
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenValidationService.validate(rawRefreshToken)
        );

        verifyNoInteractions(refreshTokenRepository, passwordEncoder);
    }

    @Test
    void validateShouldThrowRefreshTokenNotFoundWhenTokenIdDoesNotExist() {

        // Arrange
        UUID tokenId = UUID.randomUUID();
        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                RefreshTokenNotFoundException.class,
                () -> refreshTokenValidationService.validate(tokenId + ".secret")
        );
    }

    @Test
    void validateShouldThrowRefreshTokenNotFoundWhenSecretDoesNotMatch() {

        // Arrange
        UUID tokenId = UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .tokenHash("token-hash")
                .revoked(false)
                .expiresAt(LocalDateTime.of(2026, 1, 1, 10, 5, 0))
                .build();

        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(refreshToken));
        when(passwordEncoder.matches("invalid-secret", "token-hash")).thenReturn(false);

        // Act + Assert
        assertThrows(
                RefreshTokenNotFoundException.class,
                () -> refreshTokenValidationService.validate(tokenId + ".invalid-secret")
        );
    }

    @Test
    void validateShouldThrowRefreshTokenRevokedWhenTokenIsRevoked() {

        // Arrange
        UUID tokenId = UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .tokenHash("token-hash")
                .revoked(true)
                .expiresAt(LocalDateTime.of(2026, 1, 1, 10, 5, 0))
                .build();

        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(refreshToken));
        when(passwordEncoder.matches("secret", "token-hash")).thenReturn(true);

        // Act + Assert
        assertThrows(
                RefreshTokenRevokedException.class,
                () -> refreshTokenValidationService.validate(tokenId + ".secret")
        );
    }

    @Test
    void validateShouldThrowRefreshTokenExpiredWhenTokenHasExpired() {

        // Arrange
        UUID tokenId = UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .tokenHash("token-hash")
                .revoked(false)
                .expiresAt(LocalDateTime.of(2026, 1, 1, 9, 59, 0))
                .build();

        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(refreshToken));
        when(passwordEncoder.matches("secret", "token-hash")).thenReturn(true);

        // Act + Assert
        assertThrows(
                RefreshTokenExpiredException.class,
                () -> refreshTokenValidationService.validate(tokenId + ".secret")
        );
    }
}


