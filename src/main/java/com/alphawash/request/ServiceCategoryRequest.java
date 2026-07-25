package com.alphawash.request;

public record ServiceCategoryRequest(
        String name,
        String code,
        String description,
        Integer sortOrder,
        Boolean active) {}
