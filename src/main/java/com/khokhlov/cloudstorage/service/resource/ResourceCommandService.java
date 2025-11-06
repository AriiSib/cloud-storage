package com.khokhlov.cloudstorage.service.resource;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.minio.StorageException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.mapper.ResourceMapper;
import com.khokhlov.cloudstorage.model.dto.response.MinioResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import com.khokhlov.cloudstorage.util.StorageObjectBuilder;
import com.khokhlov.cloudstorage.validation.PathValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.khokhlov.cloudstorage.util.PathUtil.*;

@Service
@RequiredArgsConstructor
public class ResourceCommandService {
    private final StoragePort storage;
    private final ResourceMapper resourceMapper;

    public ResourceResponse createDirectory(long userId, String relPath) {
        String objectName = StorageObjectBuilder.normalizePath(userId, relPath);
        String parent = objectName.replace(getDirectory(relPath), "");
        if (!storage.isResourceExists(parent) && !parent.equals(StorageObjectBuilder.getUserRoot(userId)))
            throw new StorageNotFoundException("Parent directory not exist");
        if (storage.isResourceExists(objectName))
            throw new StorageAlreadyExistsException(getDirectory(relPath));
        storage.createDirectory(objectName);
        return resourceMapper.toResponse(objectName, null);
    }

    public List<ResourceResponse> uploadResource(long userId, String relPath, List<MultipartFile> files) {
        for (MultipartFile file : files)
            if (!PathValidationUtils.isValidPath(file.getOriginalFilename(), true))
                throw new StorageException("Value is longer than 255 characters or contains invalid characters");

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = StorageObjectBuilder.normalizePath(userId, relPath, filename);
            if (storage.checkObject(objectName) != null)
                throw new StorageAlreadyExistsException(filename);
        }

        List<ResourceResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectName = StorageObjectBuilder.normalizePath(userId, relPath, filename);
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

    public void deleteResource(long userId, String relPath) {
        String objectName = StorageObjectBuilder.normalizePath(userId, relPath);
        boolean isDir = isDirectory(objectName);
        if (isDir) {
            if (!storage.isResourceExists(objectName)) {
                throw new StorageNotFoundException("Resource not found");
            } else {
                List<String> objectsToDelete = storage.listObjects(objectName, true);
                storage.delete(objectsToDelete);
                return;
            }
        } else if (storage.checkObject(objectName) == null) {
            throw new StorageNotFoundException("Resource not found");
        }

        storage.delete(List.of(objectName));
    }

    public ResourceResponse moveResource(long userId, String pathFrom, String pathTo) {
        if (pathFrom.equals(pathTo)) throw new StorageAlreadyExistsException("");

        String objectNameFrom = StorageObjectBuilder.normalizePath(userId, pathFrom);
        String objectNameTo = StorageObjectBuilder.normalizePath(userId, pathTo);

        if (!storage.isResourceExists(objectNameFrom)) throw new StorageNotFoundException("Resource not found");
        boolean isDirFrom = isDirectory(objectNameFrom);
        boolean isDirTo = isDirectory(objectNameTo);

        if (isDirFrom || isDirTo) {
            if (!isDirFrom || !isDirTo) throw new StorageException("Passed paths to different resources");
            return renameOrMoveDir(objectNameFrom, objectNameTo);
        } else {
            return renameOrMoveFile(objectNameFrom, objectNameTo);
        }
    }

    private ResourceResponse renameOrMoveFile(String objectNameFrom, String objectNameTo) {
        if (storage.isResourceExists(objectNameTo))
            throw new StorageAlreadyExistsException(getFileName(objectNameTo));
        MinioResponse meta = storage.checkObject(objectNameFrom);
        if (meta == null) throw new StorageNotFoundException("Resource not found");
        storage.renameOrMove(objectNameFrom, objectNameTo);
        return resourceMapper.toResponse(objectNameTo, meta.size());
    }

    private ResourceResponse renameOrMoveDir(String objectNameFrom, String objectNameTo) {
        String dirNameFrom = getDirectory(objectNameFrom);
        String dirNameTo = getDirectory(objectNameTo);

        if (!getParentOfDir(objectNameFrom).equals(getParentOfDir(objectNameTo)) && !dirNameFrom.equals(dirNameTo))
            throw new StorageException("Names of the moved directories do not match");
        if (storage.isResourceExists(objectNameTo))
            throw new StorageAlreadyExistsException(dirNameTo);

        storage.renameOrMove(objectNameFrom, objectNameTo);
        return resourceMapper.toResponse(objectNameTo, null);
    }

}
