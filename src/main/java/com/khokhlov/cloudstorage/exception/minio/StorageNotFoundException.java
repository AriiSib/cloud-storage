package com.khokhlov.cloudstorage.exception.minio;

public class StorageNotFoundException extends StorageException {
    public StorageNotFoundException(String message) {
        super(message);
    }
}
