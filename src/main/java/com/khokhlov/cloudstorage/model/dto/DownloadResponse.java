package com.khokhlov.cloudstorage.model.dto;

import org.springframework.http.ContentDisposition;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadResponse(
        StreamingResponseBody body,
        ContentDisposition contentDisposition
) {
}