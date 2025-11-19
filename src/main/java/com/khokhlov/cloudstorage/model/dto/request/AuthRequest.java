package com.khokhlov.cloudstorage.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request with username and password")
public record AuthRequest(
        @Schema(example = "JohnDoe")
        @NotBlank @Size(min = 5, max = 20) String username,

        @Schema(example = "123_Ab.()+-{}@")
        @NotBlank @Size(min = 6, max = 32) String password
) {
}
