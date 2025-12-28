package com.alphawash.request;

public record ModelRequest(
        String modelCode,
        String modelName,
        String size,
        String brandCode,
        String brandName,
        String note) {
}