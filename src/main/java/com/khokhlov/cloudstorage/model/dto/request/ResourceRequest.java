package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePath;
import jakarta.validation.constraints.NotBlank;

public record ResourceRequest(@SafePath String path) {
}
