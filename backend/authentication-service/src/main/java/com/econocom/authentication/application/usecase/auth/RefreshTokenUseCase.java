package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.application.service.auth.RefreshTokenValidationService;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final UserRepositoryPort userRepository;

    private final AuthenticationTokenService authenticationTokenService;

    private final RefreshTokenValidationService refreshTokenValidationService;

    @Transactional
    public TokenResponse execute(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenValidationService.validate(request.getRefreshToken());

        User user = loadUser(refreshToken.getUserId());

        refreshTokenRepository.revokeById(refreshToken.getId());

        return authenticationTokenService.issue(user);

    }


    private User loadUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    }

}


