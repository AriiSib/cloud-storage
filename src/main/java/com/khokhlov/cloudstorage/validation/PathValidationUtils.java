package com.khokhlov.cloudstorage.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathValidationUtils {
    private static final int MAX_LENGTH = 200;
    private static final String SEGMENT_REGEX = "^[a-zA-Zа-яА-Я0-9 _().\\[\\]{}+@-]+$";

    public static boolean isValidPath(String value, boolean allowBlankRoot) {
        if (value == null || value.isBlank()) return allowBlankRoot;
        if (value.length() > MAX_LENGTH) return false;
        if (value.startsWith("/")) return false;
        if (value.contains("\\")) return false;

        String[] segments = value.split("/");
        for (String segment : segments) {
            if (segment.isEmpty()) return false;
            if (segment.equals(".") || segment.equals("..")) return false;
            if (!segment.matches(SEGMENT_REGEX)) return false;
        }

        return true;
    }
}
