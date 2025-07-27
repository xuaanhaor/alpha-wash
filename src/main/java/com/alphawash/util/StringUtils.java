package com.alphawash.util;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class StringUtils {
    public static boolean isNullOrBlank(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotNullOrBlank(String str) {
        return !isNullOrBlank(str);
    }

    public static <T> List<T> splitIntoList(String delimiter, String input, Function<String, T> mapper) {
        if (isNullOrBlank(input)) {
            return List.of();
        }
        return Arrays.stream(input.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(mapper)
                .toList();
    }

    public static boolean isUUIDNullOrBlank(UUID id) {
        String idStr = id.toString();
        return idStr == null || idStr.isBlank();
    }
}
