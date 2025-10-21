package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.model.dto.MinioResponse;

import java.io.InputStream;
import java.util.List;

public interface StoragePort {

    void save(String normalizedPath, InputStream inputStream, long size, String contentType);

    InputStream download(String objectName);

    MinioResponse checkObject(String objectName);

    boolean isDirectoryExists(String objectName);

    List<String> listObjects(String userRoot);

    void delete(List<String> objectNames);
}
