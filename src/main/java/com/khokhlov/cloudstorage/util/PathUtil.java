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
        return string.endsWith("/") ? string.substring(0, string.length() - 1) : string;
    }

    public static boolean isDirectory(String path) {
        return path.endsWith("/");
    }

    public static String getParentOfDir(String path) {
        String withoutSlash = stripTrailingSlash(path);
        if (!withoutSlash.contains("/")) return "/";
        int index = withoutSlash.lastIndexOf('/');
        return (index < 0) ? "" : withoutSlash.substring(0, index + 1);
    }

    public static String getParentOfFile(String path) {
        path = path.contains("/") ? path : "/" + path;
        int index = path.lastIndexOf("/");
        return (index < 0) ? path : path.substring(0, index + 1);
    }

    public static String getDirName(String path) {
        String withoutSlash = stripTrailingSlash(path);
        int index = withoutSlash.lastIndexOf('/');
        return (index < 0) ? withoutSlash : withoutSlash.substring(index + 1);
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return (index < 0) ? path : path.substring(index + 1);
    }

}
