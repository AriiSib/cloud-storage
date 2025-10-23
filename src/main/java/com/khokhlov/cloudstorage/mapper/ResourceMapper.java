package com.khokhlov.cloudstorage.mapper;

import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import org.springframework.stereotype.Component;

import static com.khokhlov.cloudstorage.model.entity.FileType.*;
import static com.khokhlov.cloudstorage.util.PathUtil.*;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(String objectName, Long size) {
        String relPath = stripUserRoot(objectName);
        if (isDirectory(relPath)) {
            return new ResourceResponse(getParentOfDir(relPath), getDirName(relPath), size, DIRECTORY);
        } else {
            return new ResourceResponse(getParentOfFile(relPath), getFileName(relPath), size, FILE);
        }
    }

}
