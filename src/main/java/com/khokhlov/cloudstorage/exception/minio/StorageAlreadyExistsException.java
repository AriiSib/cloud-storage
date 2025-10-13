package com.khokhlov.cloudstorage.exception.minio;

public class StorageAlreadyExistsException extends StorageException {
    public StorageAlreadyExistsException(String filename) {
        super("File already exists: " + filename);
    }
}
