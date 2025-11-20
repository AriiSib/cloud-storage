package com.khokhlov.cloudstorage.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PathUtil {
    private static final Pattern USER_ROOT = Pattern.compile("^user-[^/]+-files/");

    public static String stripUserRoot(String normalizedPath) {
        Matcher matcher = USER_ROOT.matcher(normalizedPath);
        if (!matcher.find())
            return normalizedPath;
        return normalizedPath.substring(matcher.end());
    }

    public static String stripTrailingSlash(String string) {
        return isDirectory(string) ? string.substring(0, string.length() - 1) : string;
    }

    public static boolean isDirectory(String path) {
        return path.endsWith("/");
    }

    public static String getParentOfDir(String path) {
        String withoutSlash = stripTrailingSlash(path);
        if (!withoutSlash.contains("/")) return "";
        int index = withoutSlash.lastIndexOf('/');
        return (index < 0) ? "" : withoutSlash.substring(0, index + 1);
    }

    public static String getFileName(String path) {
        int parent = path.lastIndexOf("/");
        return (parent < 0) ? path : path.substring(parent + 1);
    }

    public static String getDirName(String path) {
        path = stripUserRoot(path);
        String dirName = stripTrailingSlash(path);
        int parent = dirName.lastIndexOf('/');
        return (parent < 0) ? dirName : dirName.substring(parent + 1);
    }

    public static String getDirectory(String path) {
        return getDirName(path) + "/";
    }

}
