package com.khokhlov.cloudstorage.mapper;

import com.khokhlov.cloudstorage.model.dto.response.AuthResponse;
import com.khokhlov.cloudstorage.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    AuthResponse toResponse(User user);

}
