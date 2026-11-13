package org.store.authsvc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.authsvc.dtos.persistence.UserSessionsDTO;
import org.store.authsvc.entites.UserSessionsEntity;

@Mapper(componentModel = "spring")
public interface UserSessionsMap {

    @Mapping(target = "userSessionId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UserSessionsEntity toEntity(UserSessionsDTO dto);

    UserSessionsDTO toDTO(UserSessionsEntity entity);
}
