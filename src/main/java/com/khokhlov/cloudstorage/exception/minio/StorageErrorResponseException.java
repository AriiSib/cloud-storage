package com.khokhlov.cloudstorage.exception.minio;

public class StorageErrorResponseException extends StorageException {
    public StorageErrorResponseException(String message) {
        super(message);
    }
}
