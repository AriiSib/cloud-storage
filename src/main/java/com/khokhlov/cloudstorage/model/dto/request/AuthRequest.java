package com.khokhlov.cloudstorage.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Size(min = 4, max = 20) String username,
        @NotBlank @Size(min = 6, max = 32) String password
) {
}
