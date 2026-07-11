package com.econocom.authentication.infrastructure.persistence.repository;

import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenEntity rt " +
           "SET rt.revoked = true " +
           "WHERE rt.user.id = :userId " +
           "AND rt.revoked = false")
    int revokeAllActiveByUserId(@Param("userId") UUID userId); //Numero de filas afectadas por la actualización

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenEntity rt " +
           "SET rt.revoked = true " +
           "WHERE rt.id = :id " +
           "AND rt.revoked = false")
    int revokeById(@Param("id") UUID id);
}
