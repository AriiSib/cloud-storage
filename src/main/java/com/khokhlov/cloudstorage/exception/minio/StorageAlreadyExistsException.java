package com.khokhlov.cloudstorage.exception.minio;

public class StorageAlreadyExistsException extends StorageException {

    public StorageAlreadyExistsException(String resource) {
        super(messageFor(resource));
    }

    public StorageAlreadyExistsException(String resource, Throwable cause) {
        super(messageFor(resource), cause);
    }

    private static String messageFor(String resource) {
        return "Resource already exists: " + resource;
    }

}
