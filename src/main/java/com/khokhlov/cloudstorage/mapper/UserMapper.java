package com.khokhlov.cloudstorage.mapper;

import com.khokhlov.cloudstorage.model.dto.RegisterUserRequest;
import com.khokhlov.cloudstorage.model.dto.RegisterUserResponse;
import com.khokhlov.cloudstorage.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", expression = "java(request.username().trim().toLowerCase())")
    User fromRequest(RegisterUserRequest request);

    RegisterUserResponse toResponse(User user);
}
