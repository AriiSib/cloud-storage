package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.mapper.ResourceMapper;
import com.khokhlov.cloudstorage.model.dto.MinioResponse;
import com.khokhlov.cloudstorage.model.dto.ResourceResponse;
import com.khokhlov.cloudstorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final CurrentUser currentUser;
    private final StoragePort storage;
    private final ResourceMapper resourceMapper;

    public ResourceResponse checkResource(String relPath) {
        Long userId = currentUser.getCurrentUserId();
        String objectName = normalizePath(userId, relPath, "");
        boolean isDir = objectName.endsWith("/");

        if (isDir) {
            if (!storage.isDirectoryExists(objectName))
                throw new StorageNotFoundException("Resource not found");
            return resourceMapper.toResponse(objectName, null);
        } else {
            MinioResponse meta = storage.checkObject(objectName);
            if (meta == null)
                throw new StorageNotFoundException("Resource not found");
            return resourceMapper.toResponse(objectName, meta.size());
        }
    }

    public List<ResourceResponse> searchResource(String query) {
        Long userId = currentUser.getCurrentUserId();
        query = query.toLowerCase().trim();
        String userRoot = getUserRoot(userId);
        List<String> objects = storage.listObjects(userRoot);
        if (objects.isEmpty())
            throw new StorageNotFoundException("Resource not found");

        List<ResourceResponse> responses = new ArrayList<>();
        Set<String> uniqDir = new LinkedHashSet<>();

        for (String objectName : objects) {
            String relPath = objectName.substring(userRoot.length());

            String fileName = PathUtil.getFileName(relPath);
            if (fileName.toLowerCase(Locale.ROOT).contains(query)) {
                responses.add(checkResource(relPath));
            }

            String[] parts = relPath.split("/");
            StringBuilder parent = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                String currentDir = parts[i] + "/";
                if (currentDir.toLowerCase(Locale.ROOT).contains(query)) {
                    String matchDir = parent + currentDir;
                    if (uniqDir.add(matchDir)) {
                        responses.add(resourceMapper.toResponse(matchDir, null));
                    }
                }
                parent.append(currentDir);
            }
        }
        if (responses.isEmpty())
            throw new StorageNotFoundException("Resource not found");

        return responses;
    }

    public List<ResourceResponse> upload(String relPath, List<MultipartFile> files) {
        Long userId = currentUser.getCurrentUserId();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = normalizePath(userId, relPath, filename);
            if (storage.checkObject(objectName) != null)
                throw new StorageAlreadyExistsException(filename);
        }

        List<ResourceResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = normalizePath(userId, relPath, filename);
            long size = file.getSize();
            String contentType = Optional.ofNullable(file.getContentType())
                    .filter(value -> !value.isEmpty())
                    .orElse("application/octet-stream");

            try (InputStream stream = file.getInputStream()) {
                storage.save(objectName, stream, size, contentType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            responses.add(resourceMapper.toResponse(objectName, size));
        }

        return responses;
    }

    public void delete(String relPath) {
        Long userId = currentUser.getCurrentUserId();
        String objectName = normalizePath(userId, relPath, "");
        boolean isDir = relPath.endsWith("/");
        if (isDir) {
            if (!storage.isDirectoryExists(objectName)) {
                throw new StorageNotFoundException("Resource not found");
            } else {
                List<String> objectsToDelete = storage.listObjects(objectName);
                storage.delete(objectsToDelete);
                return;
            }
        } else if (storage.checkObject(objectName) == null) {
            throw new StorageNotFoundException("Resource not found");
        }

        storage.delete(List.of(objectName));
    }

    private String normalizePath(Long userId, String relPath, String fileName) {
        return getUserRoot(userId) + relPath + fileName;
    }

    private String getUserRoot(Long userId) {
        return "user-" + userId + "-files/";
    }

}
