package com.khokhlov.cloudstorage.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khokhlov.cloudstorage.model.entity.FileType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponse(
        String path,
        String name,
        Long size,
        FileType type
) {
}
