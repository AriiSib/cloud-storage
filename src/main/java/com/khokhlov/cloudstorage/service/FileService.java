package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.mapper.ResourceMapper;
import com.khokhlov.cloudstorage.model.dto.MinioResponse;
import com.khokhlov.cloudstorage.model.dto.ResourceResponse;
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
    private final ResourceMapper resourceMapper;

    public ResourceResponse checkResource(String path) {
        Long userId = currentUser.getCurrentUserId();
        String normalizedPath = normalizePath(userId, path, "");
        FileType type = getType(normalizedPath);
        if (type == DIRECTORY) {
            if (!storage.isDirectoryExists(normalizedPath)) {
                throw new StorageNotFoundException("Resource not found");
            } else {
                return resourceMapper.toResponse(path, null);
            }
        }
        MinioResponse storageObject = storage.checkObject(normalizedPath);
        if (storageObject == null)
            throw new StorageNotFoundException("Resource not found");
        long size = storageObject.size();

        return resourceMapper.toResponse(path, size);
    }

    public List<ResourceResponse> upload(String path, List<MultipartFile> files) {
        Long userId = currentUser.getCurrentUserId();
        checkFileExists(userId, path, files);

        List<ResourceResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String normalizedPath = normalizePath(userId, path, filename);
            long size = file.getSize();
            String contentType = Optional.ofNullable(file.getContentType())
                    .filter(value -> !value.isEmpty())
                    .orElse("application/octet-stream");

            try (InputStream stream = file.getInputStream()) {
                storage.save(normalizedPath, stream, size, contentType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            path = path + filename;
            responses.add(resourceMapper.toResponse(path, size));
        }

        return responses;
    }

    private void checkFileExists(Long userId, String path, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String normalizedPath = normalizePath(userId, path, filename);
            if (storage.checkObject(normalizedPath) != null)
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
