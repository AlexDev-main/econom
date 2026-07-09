package com.econocom.authentication.infrastructure.persistence.mapper;

import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

}