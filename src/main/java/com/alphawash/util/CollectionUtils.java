package com.alphawash.util;

public class CollectionUtils {
    public static <T> boolean isEmpty(Iterable<T> collection) {
        return collection == null || !collection.iterator().hasNext();
    }

    public static <T> boolean isNotEmpty(Iterable<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> boolean contains(Iterable<T> collection, T element) {
        if (isEmpty(collection)) {
            return false;
        }
        for (T item : collection) {
            if (item.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
