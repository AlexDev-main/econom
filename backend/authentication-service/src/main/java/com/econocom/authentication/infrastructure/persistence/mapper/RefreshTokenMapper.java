package com.econocom.authentication.infrastructure.persistence.mapper;

import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "userId", source = "user.id")
    RefreshToken toDomain(RefreshTokenEntity entity);

    @Mapping(target = "user", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken domain);

}
