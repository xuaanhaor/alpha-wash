package com.alphawash.request;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemRequest(
        UUID serviceId,
        String serviceName,
        String brand,
        String typeDetail,
        String warranty,
        BigDecimal price,
        boolean bonus,
        int sortOrder) {}
