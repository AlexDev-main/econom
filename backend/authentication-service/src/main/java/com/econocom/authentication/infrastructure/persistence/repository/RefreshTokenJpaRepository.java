package com.econocom.authentication.infrastructure.persistence.repository;

import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    List<RefreshTokenEntity> findAllByUser(UserEntity user);

}
