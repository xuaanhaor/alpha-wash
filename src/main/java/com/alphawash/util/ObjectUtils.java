package com.alphawash.util;

import java.util.function.Consumer;

public class ObjectUtils<T> {

    public static <T> boolean isNull(T obj) {
        return obj == null;
    }

    public static <T> boolean isNotNull(T obj) {
        return !isNull(obj);
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value == null) return;

        if (value instanceof String str) {
            if (!str.isBlank()) {
                setter.accept(value);
            }
        } else {
            setter.accept(value);
        }
    }
}
