package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.LoginRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepositoryPort userRepository;

    private final PasswordEncoderPort passwordEncoder;

    private final JwtProviderPort jwtProvider;

    private final RefreshTokenProviderPort refreshTokenProvider;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public TokenResponse execute(LoginRequest request) {

        // Implementaremos el flujo paso a paso

        return null;

    }

}
