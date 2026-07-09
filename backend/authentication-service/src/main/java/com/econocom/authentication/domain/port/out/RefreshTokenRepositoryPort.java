package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findById(UUID id);

    List<RefreshToken> findAllByUser(User user);

    void revokeAll(User user);

}
