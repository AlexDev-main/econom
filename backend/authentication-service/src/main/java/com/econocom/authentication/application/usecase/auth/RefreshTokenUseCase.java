package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.domain.exception.RefreshTokenExpiredException;
import com.econocom.authentication.domain.exception.RefreshTokenNotFoundException;
import com.econocom.authentication.domain.exception.RefreshTokenRevokedException;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final PasswordEncoderPort passwordEncoder;

    private final UserRepositoryPort userRepository;

    private final AuthenticationTokenService authenticationTokenService;

    private final Clock clock;

    @Transactional
    public TokenResponse execute(RefreshTokenRequest request) {

        RefreshToken refreshToken = findByRawToken(request.getRefreshToken());

        validateRefreshToken(refreshToken);

        User user = loadUser(refreshToken.getUserId());

        refreshTokenRepository.revokeById(refreshToken.getId());

        return authenticationTokenService.issue(user);

    }

    private RefreshToken findByRawToken(String rawRefreshToken) {

        return refreshTokenRepository.findAll().stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getTokenHash()))
                .findFirst()
                .orElseThrow(RefreshTokenNotFoundException::new);

    }

    private void validateRefreshToken(RefreshToken refreshToken) {

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenRevokedException();
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            throw new RefreshTokenExpiredException();
        }

    }

    private User loadUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    }

}


