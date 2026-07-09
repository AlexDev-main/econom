package com.econocom.authentication.infrastructure.persistence.adapter;

import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.mapper.RefreshTokenMapper;
import com.econocom.authentication.infrastructure.persistence.mapper.UserMapper;
import com.econocom.authentication.infrastructure.persistence.repository.RefreshTokenJpaRepository;
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
    private final UserMapper userMapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return mapper.toDomain(repository.save(mapper.toEntity(refreshToken)));
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findAllByUser(User user) {
        return repository.findAllByUser(userMapper.toEntity(user))
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAll(User user) {

        List<RefreshTokenEntity> tokens =
                repository.findAllByUser(userMapper.toEntity(user));

        tokens.forEach(token -> token.setRevoked(true));

        repository.saveAll(tokens);
    }

}