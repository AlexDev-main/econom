package com.econocom.authentication.application.service.auth;

import com.econocom.authentication.domain.exception.InvalidRefreshTokenException;
import com.econocom.authentication.domain.exception.RefreshTokenExpiredException;
import com.econocom.authentication.domain.exception.RefreshTokenNotFoundException;
import com.econocom.authentication.domain.exception.RefreshTokenRevokedException;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenValidationService {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final PasswordEncoderPort passwordEncoder;

    private final Clock clock;

    public RefreshToken validate(String rawRefreshToken) {

        ParsedRefreshToken parsed = parse(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findById(parsed.tokenId)
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (!passwordEncoder.matches(parsed.secret, refreshToken.getTokenHash())) {
            throw new RefreshTokenNotFoundException();
        }

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenRevokedException();
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            throw new RefreshTokenExpiredException();
        }

        return refreshToken;

    }

    private ParsedRefreshToken parse(String rawRefreshToken) {

        if (rawRefreshToken == null || rawRefreshToken.trim().isEmpty()) {
            throw new InvalidRefreshTokenException();
        }

        int separatorIndex = rawRefreshToken.indexOf('.');

        if (separatorIndex <= 0 || separatorIndex == rawRefreshToken.length() - 1) {
            throw new InvalidRefreshTokenException();
        }

        String tokenIdText = rawRefreshToken.substring(0, separatorIndex);
        String secret = rawRefreshToken.substring(separatorIndex + 1);

        try {
            UUID tokenId = UUID.fromString(tokenIdText);
            return new ParsedRefreshToken(tokenId, secret);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRefreshTokenException();
        }

    }

    private static class ParsedRefreshToken {

        private final UUID tokenId;

        private final String secret;

        private ParsedRefreshToken(UUID tokenId, String secret) {
            this.tokenId = tokenId;
            this.secret = secret;
        }

    }

}

