package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.exception.minio.*;
import com.khokhlov.cloudstorage.model.dto.response.MinioResponse;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.khokhlov.cloudstorage.util.PathUtil.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioStorageAdapter implements StoragePort {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public boolean isResourceExists(String objectName) {
        log.debug("Checking if resource exists for={}", objectName);
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(objectName)
                            .build());
            return results.iterator().hasNext();
        } catch (Exception e) {
            throw new StorageException("Failed to check existence of resource", e);
        }
    }

    @Override
    public List<String> listObjects(String userRoot, boolean recursive) {
        log.debug("Getting a list of resources={} recursive={}", userRoot, recursive);
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(userRoot)
                            .recursive(recursive)
                            .build());
            if (!results.iterator().hasNext()) return Collections.emptyList();
            List<String> items = new ArrayList<>();
            for (Result<Item> result : results) {
                items.add(result.get().objectName());
            }
            return items;
        } catch (Exception e) {
            throw new StorageException("Failed to list objects", e);
        }
    }

    @Override
    public MinioResponse checkObject(String objectName) {
        log.debug("Getting information about a resource={}", objectName);
        try {
            StatObjectResponse response = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return new MinioResponse(response.size());
        } catch (ErrorResponseException e) {
            if ((e).errorResponse().code().equals("NoSuchKey"))
                return null;
            throw new StorageNotFoundException("Failed to get information about resource", e);
        } catch (Exception e) {
            throw new StorageException("Unexpected storage error while getting information about resource", e);
        }
    }

    @Override
    public void renameOrMove(String objectNameFrom, String objectNameTo) {
        log.debug("Renaming or moving resource from={} to={}", objectNameFrom, objectNameTo);
        List<String> objects;
        if (isDirectory(objectNameFrom)) {
            objects = listObjects(objectNameFrom, true);
            for (String from : objects) {
                String to = from.replace(objectNameFrom, objectNameTo);
                copy(from, to);
            }
        } else {
            copy(objectNameFrom, objectNameTo);
            objects = List.of(objectNameFrom);
        }
        delete(objects);
    }

    @Override
    public void copy(String from, String to) {
        log.debug("Copying resource from={} to={}", from, to);
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(to)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucketName)
                                            .object(from)
                                            .build())
                            .build());
        } catch (ErrorResponseException | InvalidResponseException e) {
            throw new StorageErrorResponseException("Failed to copy object", e);
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException("Unexpected storage error while copying", e);
        } catch (InvalidKeyException e) {
            throw new StorageAccessException("Storage access error while copying", e);
        }
    }

    @Override
    public void createDirectory(String objectName) {
        log.debug("Creating directory={}", objectName);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (ErrorResponseException | InvalidResponseException e) {
            throw new StorageErrorResponseException("Failed to create directory", e);
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException("Unexpected storage error while creating directory", e);
        } catch (InvalidKeyException e) {
            throw new StorageAccessException("Storage access error while creating directory", e);
        }
    }

    @Override
    public void save(String objectName, InputStream is, long size, String contentType) {
        log.debug("Saving resource={} size={} contentType={}", objectName, size, contentType);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (ErrorResponseException | InvalidResponseException e) {
            throw new StorageErrorResponseException("Failed to save resource", e);
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException("Unexpected storage error while saving resource", e);
        } catch (InvalidKeyException e) {
            throw new StorageAccessException("Storage access error while saving resource", e);
        }
    }

    @Override
    public InputStream download(String objectName) {
        log.debug("Downloading resource={}", objectName);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException | InvalidResponseException e) {
            throw new StorageErrorResponseException("Failed to download resource", e);
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException("Unexpected storage error while downloading resource", e);
        } catch (InvalidKeyException e) {
            throw new StorageAccessException("Storage access error while downloading resource", e);
        }
    }

    @Override
    public void delete(List<String> objectNames) {
        log.debug("Deleting resources={}", objectNames);
        List<DeleteObject> objects = new ArrayList<>();
        for (String objectName : objectNames) {
            objects.add(new DeleteObject(objectName));
        }
        List<StorageDeleteFailedException.Failure> failures = new ArrayList<>();
        try {
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder()
                                    .bucket(bucketName)
                                    .objects(objects)
                                    .build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                failures.add(new StorageDeleteFailedException.Failure(error.objectName(), error.message()));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to delete resources", e);
        }
        if (!failures.isEmpty()) {
            throw new StorageDeleteFailedException(failures);
        }
    }

}
