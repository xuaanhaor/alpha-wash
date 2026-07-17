package com.alphawash.util;

public class LicensePlateUtil {

    private LicensePlateUtil() {}

    public static String normalize(String plate) {
        if (plate == null) {
            return null;
        }
        return plate.replaceAll("[\\s-]", "").toUpperCase();
    }
}
