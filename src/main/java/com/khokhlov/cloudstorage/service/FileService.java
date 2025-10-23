package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.minio.StorageException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.mapper.ResourceMapper;
import com.khokhlov.cloudstorage.model.dto.DownloadResponse;
import com.khokhlov.cloudstorage.model.dto.MinioResponse;
import com.khokhlov.cloudstorage.model.dto.ResourceResponse;
import com.khokhlov.cloudstorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
            if (!storage.isResourceExists(objectName))
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

    public ResourceResponse renameOrMove(String pathFrom, String pathTo) {
        Long userId = currentUser.getCurrentUserId();
        if (pathFrom.equals(pathTo)) throw new StorageAlreadyExistsException("");

        String objectNameFrom = normalizePath(userId, pathFrom, "");
        String objectNameTo = normalizePath(userId, pathTo, "");

        if (!storage.isResourceExists(objectNameFrom)) throw new StorageNotFoundException("Resource not found");
        boolean isDirFrom = objectNameFrom.endsWith("/");
        boolean isDirTo = objectNameTo.endsWith("/");

        if (isDirFrom || isDirTo) {
            if (!isDirFrom || !isDirTo) throw new StorageException("Passed paths to different resources");
            return renameOrMoveDir(objectNameFrom, objectNameTo);
        } else {
            return renameOrMoveFile(objectNameFrom, objectNameTo);
        }
    }

    private ResourceResponse renameOrMoveFile(String objectNameFrom, String objectNameTo) {
        if (storage.isResourceExists(objectNameTo))
            throw new StorageAlreadyExistsException(PathUtil.getFileName(objectNameTo));
        MinioResponse meta = storage.checkObject(objectNameFrom);
        if (meta == null) throw new StorageNotFoundException("Resource not found");
        storage.renameOrMove(objectNameFrom, objectNameTo);
        return resourceMapper.toResponse(objectNameTo, meta.size());
    }

    private ResourceResponse renameOrMoveDir(String objectNameFrom, String objectNameTo) {
        String dirNameFrom = PathUtil.getDirName(objectNameFrom);
        String dirNameTo = PathUtil.getDirName(objectNameTo);

        if (storage.isResourceExists(objectNameTo + dirNameFrom))
            throw new StorageAlreadyExistsException(dirNameTo);

        storage.renameOrMove(objectNameFrom, objectNameTo);
        return resourceMapper.toResponse(objectNameTo, null);
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

    public DownloadResponse download(String path) {
        Long userId = currentUser.getCurrentUserId();
        String userRoot = getUserRoot(userId);
        String objectName = userRoot + path;
        boolean isDir = path.endsWith("/");
        if (isDir) {
            if (!storage.isResourceExists(objectName))
                throw new StorageNotFoundException("Resource not found");
            else {
                List<String> objects = storage.listObjects(objectName);
                String zipName = PathUtil.getDirName(path) + ".zip";
                ContentDisposition contentDisposition = ContentDisposition.attachment()
                        .filename(zipName).build();

                StreamingResponseBody body = out -> {
                    try (ZipOutputStream zip = new ZipOutputStream(out)) {
                        for (String filePath : objects) {
                            String relPath = filePath.substring(userRoot.length());
                            String entryName = relPath.substring(path.length());
                            ZipEntry entry = new ZipEntry(entryName);
                            zip.putNextEntry(entry);
                            try (InputStream in = storage.download(filePath)) {
                                in.transferTo(zip);
                            }
                            zip.closeEntry();
                        }
                    }
                };

                return new DownloadResponse(body, contentDisposition);
            }
        } else {
            MinioResponse meta = storage.checkObject(objectName);
            if (meta == null)
                throw new StorageNotFoundException("Resource not found");

            StreamingResponseBody body = out -> {
                try (InputStream in = storage.download(objectName)) {
                    in.transferTo(out);
                }
            };

            String fileName = PathUtil.getFileName(path);
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName).build();

            return new DownloadResponse(body, contentDisposition);
        }
    }

    public void delete(String relPath) {
        Long userId = currentUser.getCurrentUserId();
        String objectName = normalizePath(userId, relPath, "");
        boolean isDir = relPath.endsWith("/");
        if (isDir) {
            if (!storage.isResourceExists(objectName)) {
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
