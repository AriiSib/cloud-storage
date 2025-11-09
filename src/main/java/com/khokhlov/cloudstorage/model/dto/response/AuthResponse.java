package com.khokhlov.cloudstorage.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response upon successful user registration")
public record AuthResponse(
        @Schema(example = "JohnDoe")
        String username
) {
}
