package com.khokhlov.cloudstorage.exception.minio;

import lombok.Getter;

import java.util.List;

@Getter
public class StorageDeleteFailedException extends RuntimeException {
    public record Failure(String object, String message) {
    }

    private final List<Failure> failures;

    public StorageDeleteFailedException(List<Failure> failures) {
        super(message());
        this.failures = List.copyOf(failures);
    }

    public StorageDeleteFailedException(List<Failure> failures, Throwable cause) {
        super(message(), cause);
        this.failures = List.copyOf(failures);
    }

    private static String message() {
        return "Failed to delete some objects";
    }

}
