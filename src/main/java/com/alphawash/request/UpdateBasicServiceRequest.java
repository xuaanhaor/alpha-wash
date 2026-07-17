package com.alphawash.request;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateBasicServiceRequest(
        String serviceTypeCode,
        String serviceCode,
        String serviceName,
        String duration,
        String note,
        Map<String, SizeRequest> sizes) {
    public record SizeRequest(BigDecimal price) {}
}
