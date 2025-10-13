package com.khokhlov.cloudstorage.adapter;

import com.khokhlov.cloudstorage.exception.minio.StorageAccessException;
import com.khokhlov.cloudstorage.exception.minio.StorageErrorResponseException;
import com.khokhlov.cloudstorage.exception.minio.StorageException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class MinioStorageAdapter implements StoragePort {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public boolean isObjectExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.getMessage().equals("Object does not exist"))
                return false;
            throw new StorageNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
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

}
