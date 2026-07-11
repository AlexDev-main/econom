package com.econocom.authentication.infrastructure.persistence.adapter;

import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.mapper.RefreshTokenMapper;
import com.econocom.authentication.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.econocom.authentication.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository repository;
    private final RefreshTokenMapper mapper;
    private final UserJpaRepository userRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        entity.setUser(userRepository.getReferenceById(refreshToken.getUserId()));
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAllActiveByUser(UUID userId) {

        repository.revokeAllActiveByUserId(userId);

    }

    @Override
    public void revokeById(UUID id) {
        repository.revokeById(id);
    }

}