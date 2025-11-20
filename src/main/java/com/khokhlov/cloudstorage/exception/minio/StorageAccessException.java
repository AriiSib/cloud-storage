package com.khokhlov.cloudstorage.exception.minio;

public class StorageAccessException extends StorageException {
    public StorageAccessException(String message) {
        super(message);
    }

    public StorageAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
