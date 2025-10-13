package com.khokhlov.cloudstorage.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khokhlov.cloudstorage.model.entity.FileType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadResponse(
        String path,
        String name,
        Long size,
        FileType type
) {
}
