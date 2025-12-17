package com.alphawash.request;

import java.math.BigDecimal;


public record AddPromotionServicesRequest(
        String serviceCode,
        BigDecimal discountAmount,
        BigDecimal discountPercent
) {
}
