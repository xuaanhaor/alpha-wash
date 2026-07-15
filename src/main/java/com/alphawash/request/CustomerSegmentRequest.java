package com.alphawash.request;

public record CustomerSegmentRequest(
    String code,
    String segmentName,
    String description,
    String color,
    String icon,
    String conditions,
    String logicOperator,
    Integer displayOrder,
    Boolean isActive
) {}
