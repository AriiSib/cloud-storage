package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePath;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Renaming/Moving a resource")
public record RenameOrMoveRequest(
        @Schema(example = "docs/old.txt or docs/old/")
        @SafePath
        String from,

        @Schema(example = "docs/new.txt or docs/new/")
        @SafePath
        String to
) {
}
