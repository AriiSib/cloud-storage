package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePath;

public record RenameOrMoveRequest(@SafePath String from, @SafePath String to) {
}
