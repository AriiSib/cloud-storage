package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.model.dto.MinioResponse;

import java.io.InputStream;

public interface StoragePort {

    void save(String normalizedPath, InputStream inputStream, long size, String contentType);

    MinioResponse checkObject(String objectName);

    boolean isDirectoryExists(String objectName);
}
