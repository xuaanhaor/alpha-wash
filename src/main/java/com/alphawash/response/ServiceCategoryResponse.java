package com.alphawash.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceCategoryResponse(
        UUID id,
        String code,
        String name,
        String description,
        int sortOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
