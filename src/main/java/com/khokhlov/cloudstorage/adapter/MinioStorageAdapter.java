package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.exception.minio.*;
import com.khokhlov.cloudstorage.model.dto.response.MinioResponse;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinioStorageAdapter implements StoragePort {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public boolean isResourceExists(String objectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(objectName)
                            .build());
            return results.iterator().hasNext();
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public List<String> listObjects(String userRoot, boolean recursive) {
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
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public MinioResponse checkObject(String objectName) {
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
            throw new StorageNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void renameOrMove(String objectNameFrom, String objectNameTo) {
        List<String> objects;
        if (objectNameFrom.endsWith("/")) {
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
            throw new StorageErrorResponseException(e.getMessage());
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new StorageAccessException(e.getMessage());
        }
    }

    @Override
    public void save(String objectName, InputStream is, long size, String contentType) {
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
            throw new StorageErrorResponseException(e.getMessage());
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new StorageAccessException(e.getMessage());
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException | InvalidResponseException e) {
            throw new StorageErrorResponseException(e.getMessage());
        } catch (InsufficientDataException | InternalException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new StorageException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new StorageAccessException(e.getMessage());
        }
    }

    @Override
    public void delete(List<String> objectNames) {
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
            throw new StorageException(e.getMessage());
        }
        if (!failures.isEmpty()) {
            throw new StorageDeleteFailedException(failures);
        }
    }

}
