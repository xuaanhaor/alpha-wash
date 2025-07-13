package com.alphawash.response;

import com.alphawash.util.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        if (data == null) {
            return new ApiResponse<>(true, "Data is null", data);
        }
        if (data instanceof Iterable<?> && !((Iterable<?>) data).iterator().hasNext()) {
            return new ApiResponse<>(true, "Data is empty", data);
        }
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
