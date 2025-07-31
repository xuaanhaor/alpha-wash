package com.alphawash.request;

import java.math.BigDecimal;

public record CreateBasicServiceRequest(
        String serviceTypeCode, String serviceName, String duration, String size, BigDecimal price, String note) {}
