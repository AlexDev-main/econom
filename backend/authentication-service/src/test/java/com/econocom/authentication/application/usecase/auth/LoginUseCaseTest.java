package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.LoginRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.domain.exception.InvalidCredentialsException;
import com.econocom.authentication.domain.exception.UserDisabledException;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private AuthenticationTokenService authenticationTokenService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void executeShouldReturnTokensWhenCredentialsAreValid() {

        // Arrange
        UUID userId = UUID.randomUUID();

        LoginRequest request = LoginRequest.builder()
                .email("admin@econocom.com")
                .password("raw-password")
                .build();

        User user = User.builder()
                .id(userId)
                .email("admin@econocom.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(900000L)
                .build();

        when(userRepository.findByEmail("admin@econocom.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw-password", "encoded-password")).thenReturn(true);
        when(authenticationTokenService.issue(user)).thenReturn(tokenResponse);

        // Act
        TokenResponse result = loginUseCase.execute(request);

        // Assert
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(refreshTokenRepository).revokeAllActiveByUser(userId);
        verify(authenticationTokenService).issue(user);
    }

    @Test
    void executeShouldThrowUserNotFoundWhenEmailDoesNotExist() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("missing@econocom.com")
                .password("raw-password")
                .build();

        when(userRepository.findByEmail("missing@econocom.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> loginUseCase.execute(request));

        verifyNoInteractions(passwordEncoder, refreshTokenRepository, authenticationTokenService);
    }

    @Test
    void executeShouldThrowUserDisabledWhenUserIsDisabled() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("admin@econocom.com")
                .password("raw-password")
                .build();

        User disabledUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@econocom.com")
                .password("encoded-password")
                .enabled(false)
                .build();

        when(userRepository.findByEmail("admin@econocom.com")).thenReturn(Optional.of(disabledUser));

        // Act + Assert
        assertThrows(UserDisabledException.class, () -> loginUseCase.execute(request));

        verify(passwordEncoder, never()).matches("raw-password", "encoded-password");
        verify(refreshTokenRepository, never()).revokeAllActiveByUser(disabledUser.getId());
        verify(authenticationTokenService, never()).issue(disabledUser);
    }

    @Test
    void executeShouldThrowInvalidCredentialsWhenPasswordDoesNotMatch() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("admin@econocom.com")
                .password("wrong-password")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@econocom.com")
                .password("encoded-password")
                .enabled(true)
                .build();

        when(userRepository.findByEmail("admin@econocom.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        // Act + Assert
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));

        verify(refreshTokenRepository, never()).revokeAllActiveByUser(user.getId());
        verify(authenticationTokenService, never()).issue(user);
    }
}


