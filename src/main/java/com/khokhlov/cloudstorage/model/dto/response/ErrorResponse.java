package com.khokhlov.cloudstorage.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response with error description")
public record ErrorResponse(String message) {
}
