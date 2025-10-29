package com.khokhlov.cloudstorage.service.resource;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.mapper.ResourceMapper;
import com.khokhlov.cloudstorage.model.dto.response.MinioResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import com.khokhlov.cloudstorage.util.StorageObjectBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.khokhlov.cloudstorage.util.PathUtil.*;

@Service
@RequiredArgsConstructor
public class ResourceQueryService {
    private final CurrentUser currentUser;
    private final StoragePort storage;
    private final ResourceMapper resourceMapper;


    public ResourceResponse checkResource(String relPath) {
        Long userId = currentUser.getCurrentUserId();
        String objectName = StorageObjectBuilder.normalizePath(userId, relPath);
        boolean isDir = isDirectory(objectName);

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


    public List<ResourceResponse> checkDirectory(String relPath) {
        Long userId = currentUser.getCurrentUserId();
        String objectName = StorageObjectBuilder.normalizePath(userId, relPath);
        if (!storage.isResourceExists(objectName) && !objectName.equals(StorageObjectBuilder.getUserRoot(userId)))
            throw new StorageNotFoundException("Resource not found");
        List<ResourceResponse> responses = new ArrayList<>();
        List<String> objects = storage.listObjects(objectName, false);
        for (String object : objects) {
            if (object.equals(objectName)) continue;
            responses.add(checkResource(stripUserRoot(object)));
        }

        return responses;
    }

    public List<ResourceResponse> searchResource(String query) {
        Long userId = currentUser.getCurrentUserId();
        query = query.toLowerCase().trim();
        String userRoot = StorageObjectBuilder.getUserRoot(userId);
        List<String> objects = storage.listObjects(userRoot, true);
        if (objects.isEmpty())
            throw new StorageNotFoundException("Resource not found");

        List<ResourceResponse> responses = new ArrayList<>();
        Set<String> uniqDir = new LinkedHashSet<>();

        for (String objectName : objects) {
            String relPath = objectName.substring(userRoot.length());

            String fileName = getFileName(relPath);
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

}
