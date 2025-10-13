package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.model.dto.UploadResponse;
import com.khokhlov.cloudstorage.model.entity.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.khokhlov.cloudstorage.model.entity.FileType.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final CurrentUser currentUser;
    private final StoragePort storage;

    public List<UploadResponse> upload(String path, List<MultipartFile> files) {
        Long userId = currentUser.getCurrentUserId();
        checkFileExists(userId, path, files);

        List<UploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = normalizePath(userId, path, filename);
            long size = file.getSize();
            FileType type = getType(objectName);
            String contentType = Optional.ofNullable(file.getContentType())
                    .filter(value -> !value.isEmpty())
                    .orElse("application/octet-stream");

            try (InputStream stream = file.getInputStream()) {
                storage.save(objectName, stream, size, contentType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            responses.add(new UploadResponse(path, filename, size, type));
        }

        return responses;
    }

    private void checkFileExists(Long userId, String path, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = normalizePath(userId, path, filename);
            if (storage.isObjectExists(objectName))
                throw new StorageAlreadyExistsException(filename);
        }
    }

    private String normalizePath(Long userId, String path, String fileName) {
        return "user-" + userId + "-files/" + path + fileName;
    }

    private FileType getType(String path) {
        return path.endsWith("/") ? DIRECTORY : FILE;
    }

}
