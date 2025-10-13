package com.khokhlov.cloudstorage.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UploadRequest(
        @NotBlank
        @Size(max = 300)
        @Pattern(
                regexp = "^(?!/)(?!.*//)(?!.*\\.{2})(?:[A-Za-z0-9._\\-](?:[A-Za-z0-9._\\- ]*[A-Za-z0-9._\\-])?/)+$",
                message = "The path must point to a directory and end with '/', without '..' or '//'"
        )
        String path
) {
}
