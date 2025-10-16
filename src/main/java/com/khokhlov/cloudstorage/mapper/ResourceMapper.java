package com.khokhlov.cloudstorage.mapper;

import com.khokhlov.cloudstorage.model.dto.ResourceResponse;
import org.springframework.stereotype.Component;

import static com.khokhlov.cloudstorage.model.entity.FileType.*;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(String path, Long size) {
        if (isDirectoryPath(path)) {
            return new ResourceResponse(
                    parentDirOfDir(path),
                    dirName(path),
                    size,
                    DIRECTORY
            );
        } else {
            return new ResourceResponse(
                    parentDirOfFile(path),
                    nameOfFile(path),
                    size,
                    FILE
            );
        }
    }

    private static boolean isDirectoryPath(String path) {
        return path.endsWith("/");
    }

    private static String parentDirOfDir(String path) {
        String withoutSlash = removeTrailingSlash(path);
        if (!withoutSlash.contains("/")) return "/";
        int index = withoutSlash.lastIndexOf('/');
        return (index < 0) ? "" : withoutSlash.substring(0, index + 1);
    }

    private static String dirName(String path) {
        String withoutSlash = removeTrailingSlash(path);
        int index = withoutSlash.lastIndexOf('/');
        return (index < 0) ? withoutSlash : withoutSlash.substring(index + 1);
    }

    private static String parentDirOfFile(String path) {
        path = path.contains("/") ? path : "/" + path;
        int index = path.lastIndexOf("/");
        return (index < 0) ? path : path.substring(0, index + 1);
    }

    private static String nameOfFile(String path) {
        int index = path.lastIndexOf("/");
        return (index < 0) ? path : path.substring(index + 1);
    }

    private static String removeTrailingSlash(String string) {
        return string.endsWith("/") ? string.substring(0, string.length() - 1) : string;
    }

}
