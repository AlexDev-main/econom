package com.econocom.authentication.infrastructure.persistence.mapper;

import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface RefreshTokenMapper {

    RefreshToken toDomain(RefreshTokenEntity entity);

    RefreshTokenEntity toEntity(RefreshToken domain);

}
