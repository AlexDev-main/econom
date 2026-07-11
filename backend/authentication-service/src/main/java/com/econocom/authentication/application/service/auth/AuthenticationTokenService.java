package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.factory.RefreshTokenFactory;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenProviderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationTokenService {

    private final JwtProviderPort jwtProvider;

    private final RefreshTokenProviderPort refreshTokenProvider;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final RefreshTokenFactory refreshTokenFactory;

    public TokenResponse issue(User user) {

        String accessToken = jwtProvider.generateAccessToken(user);

        String refreshToken = refreshTokenProvider.generate();

        RefreshToken refreshTokenEntity =
                refreshTokenFactory.create(user.getId(), refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenExpiration())
                .build();

    }

}



