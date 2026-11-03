package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.UserUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.UserProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserProfileResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget User entity);
}