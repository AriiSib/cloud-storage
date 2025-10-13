package com.khokhlov.cloudstorage.adapter;

import java.io.InputStream;

public interface StoragePort {

    void save(String normalizedPath, InputStream inputStream, long size, String contentType);

    boolean isObjectExists(String objectName);
}
