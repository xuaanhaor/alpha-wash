package com.alphawash.util;

import java.lang.reflect.Field;

public class PatchHelper {

    public static <T> void applyPatch(T source, T target) {
        if (source == null || target == null) return;

        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true); // Cho phép truy cập private field
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value); // Chỉ ghi đè nếu value khác null
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to apply patch for field: " + field.getName(), e);
            }
        }
    }
}
