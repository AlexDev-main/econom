package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findById(UUID id);

    List<RefreshToken> findAll();

    void revokeAllActiveByUser(UUID userId);

    void revokeById(UUID id);

}
