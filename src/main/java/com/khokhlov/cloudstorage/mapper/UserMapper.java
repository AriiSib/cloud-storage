package com.khokhlov.cloudstorage.mapper;

import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.response.AuthResponse;
import com.khokhlov.cloudstorage.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", expression = "java(normalize(request.username()))")
    User toNewUser(AuthRequest request);

    AuthResponse toResponse(User user);

    default String normalize(String raw) {
        return raw == null ? null : raw.trim();
    }
}
