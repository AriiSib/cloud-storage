package com.khokhlov.cloudstorage.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khokhlov.cloudstorage.model.entity.FileType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Answer by resource")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponse(
        @Schema(description = "Path to the directory where the resource is located", example = "folder/file.txt")
        String path,
        @Schema(description = "Resource name", example = "file.txt")
        String name,
        @Schema(description = "Resource size in bytes. Hidden for directories", example = "123", nullable = true)
        Long size,
        @Schema(implementation = FileType.class)
        FileType type
) {
}
