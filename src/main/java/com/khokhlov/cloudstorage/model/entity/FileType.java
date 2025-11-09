package com.khokhlov.cloudstorage.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resource type")
public enum FileType {
    FILE,
    DIRECTORY
}
