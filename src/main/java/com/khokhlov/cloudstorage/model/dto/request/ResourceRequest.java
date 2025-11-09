package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePath;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Query with relative path (URL-encoded)")
public record ResourceRequest(
        @Schema(example = "docs/file.txt or docs/folder/")
        @SafePath
        String path
) {
}
