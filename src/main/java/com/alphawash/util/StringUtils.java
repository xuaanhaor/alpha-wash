package com.alphawash.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static <T> List<T> splitIntoList(String delimiter, String input, Function<String, T> mapper) {
        if (isNullOrEmpty(input)) {
            return List.of();
        }
        return Arrays.stream(input.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(mapper)
                .toList();
    }
}
