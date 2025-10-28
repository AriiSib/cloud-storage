package com.khokhlov.cloudstorage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StorageObjectBuilder {
    public static final String USER_ROOT_FORMAT = "user-%d-files/";

    public static String getUserRoot(Long userId) {
        return String.format(USER_ROOT_FORMAT, userId);
    }

    public static String normalizePath(Long userId, String relPath) {
        return getUserRoot(userId) + relPath;
    }

    public static String normalizePath(Long userId, String relPath, String fileName) {
        return getUserRoot(userId) + relPath + fileName;
    }
}
