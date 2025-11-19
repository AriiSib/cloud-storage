package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePathOrRoot;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Query with relative path (URL-encoded). Can be empty if path is root")
public record RootOrResourceRequest(
        @Schema(example = "docs/file.txt or docs/folder/ or 'empty' ")
        @SafePathOrRoot
        String path) {
}
