package com.alphawash.util;

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
}
