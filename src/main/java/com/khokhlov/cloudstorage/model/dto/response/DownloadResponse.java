package com.khokhlov.cloudstorage.model.dto.response;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadResponse(
        StreamingResponseBody body,
        String contentDisposition
) {
}