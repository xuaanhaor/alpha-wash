package com.alphawash.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }

    public static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDate convertDateFormat(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
        return LocalDate.parse(input, formatter);
    }
}
