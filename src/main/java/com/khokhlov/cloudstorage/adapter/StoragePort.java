package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.model.dto.MinioResponse;

import java.io.InputStream;
import java.util.List;

public interface StoragePort {

    void save(String normalizedPath, InputStream inputStream, long size, String contentType);

    void copy(String from, String to);

    void renameOrMove(String objectNameFrom, String objectNameTo);

    InputStream download(String objectName);

    MinioResponse checkObject(String objectName);

    boolean isResourceExists(String objectName);

    List<String> listObjects(String userRoot);

    void delete(List<String> objectNames);
}
