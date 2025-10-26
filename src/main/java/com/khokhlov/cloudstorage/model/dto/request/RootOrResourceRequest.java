package com.khokhlov.cloudstorage.model.dto.request;

import com.khokhlov.cloudstorage.validation.annotation.SafePathOrRoot;

public record RootOrResourceRequest(@SafePathOrRoot String path) {
}
