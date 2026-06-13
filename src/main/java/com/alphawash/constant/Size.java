package com.alphawash.constant;

import com.alphawash.exception.InvalidArgumentException;
import com.alphawash.util.StringUtils;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Size {
    S("S"),
    M("M"),
    L("L"),
    XL("XL");

    private final String value;

    Size(String value) {
        this.value = value;
    }

    /**
     * Checks if the provided value is a valid size.
     *
     * @param value the size value to check
     * @return true if the value is valid, false otherwise
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        return Arrays.stream(Size.values()).anyMatch(s -> s.value.equalsIgnoreCase(value));
    }

    /**
     * Converts a string to a Size enum.
     *
     * @param value the string value to convert
     * @return the corresponding Size enum
     * @throws InvalidArgumentException if the value is null, blank, or does not match any Size enum
     */
    public static Size fromString(String value) {
        String msg = StringUtils.isNullOrBlank(value) ? ErrorConst.E001 : value;
        return Arrays.stream(Size.values())
                .filter(s -> s.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException(msg));
    }
}
