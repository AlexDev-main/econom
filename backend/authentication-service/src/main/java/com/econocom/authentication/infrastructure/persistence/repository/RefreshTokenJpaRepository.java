package com.econocom.authentication.infrastructure.persistence.repository;

import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    List<RefreshTokenEntity> findAllByUser(UserEntity user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenEntity rt " +
           "SET rt.revoked = true " +
           "WHERE rt.user = :user " +
           "AND rt.revoked = false")
    int revokeAllByUser(UserEntity user); //Numero de filas afectadas por la actualización
}
