package com.khokhlov.cloudstorage.exception.minio;

import lombok.Getter;

import java.util.List;

@Getter
public class StorageDeleteFailedException extends RuntimeException {
    public record Failure(String object, String message) {}
    private final List<Failure> failures;

    public StorageDeleteFailedException(List<Failure> failures) {
        super("Failed to delete some objects");
        this.failures = List.copyOf(failures);
    }

}
