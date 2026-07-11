package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findById(UUID id);


    void revokeAllActiveByUser(UUID userId);

    void revokeById(UUID id);

}
