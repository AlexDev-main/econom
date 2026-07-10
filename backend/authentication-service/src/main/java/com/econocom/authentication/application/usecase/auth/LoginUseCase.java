package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.LoginRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.factory.RefreshTokenFactory;
import com.econocom.authentication.domain.exception.InvalidCredentialsException;
import com.econocom.authentication.domain.exception.UserDisabledException;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepositoryPort userRepository;

    private final PasswordEncoderPort passwordEncoder;

    private final JwtProviderPort jwtProvider;

    private final RefreshTokenProviderPort refreshTokenProvider;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final RefreshTokenFactory refreshTokenFactory;

    @Transactional
    public TokenResponse execute(LoginRequest request) {

        User user = loadUser(request.getEmail());

        validateUser(user);

        validatePassword(
                request.getPassword(),
                user.getPassword()
        );

        return createAuthentication(user);

    }

    private User loadUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(email));

    }

    private void validateUser(User user) {

        if (!user.isEnabled()) {
            throw new UserDisabledException();
        }

    }

    private void validatePassword(
            String rawPassword,
            String encodedPassword) {

        if (!passwordEncoder.matches(
                rawPassword,
                encodedPassword)) {

            throw new InvalidCredentialsException();

        }

    }

    private TokenResponse createAuthentication(User user) {

        String accessToken = jwtProvider.generateAccessToken(user);

        String refreshToken = refreshTokenProvider.generate();

        RefreshToken refreshTokenEntity =
                refreshTokenFactory.create(user, refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(
                        jwtProvider.getAccessTokenExpiration()
                )
                .build();

    }

}
