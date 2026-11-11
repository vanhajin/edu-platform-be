package org.store.authsvc.mappers;

import com.eduplatform.common.entites.UsersEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.authsvc.dtos.request.RegistorRequestDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UsersEntity toEntity(RegistorRequestDTO request);
}
