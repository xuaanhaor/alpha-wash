package com.alphawash.request;

import com.alphawash.constant.ServiceCategory;
import java.math.BigDecimal;

public record ServiceItemRequest(
        String name,
        ServiceCategory category,
        String brand,
        String typeDetail,
        String warranty,
        BigDecimal priceS,
        BigDecimal priceM,
        BigDecimal priceL,
        BigDecimal priceSEDAN,
        BigDecimal priceSUV,
        BigDecimal priceOverSize,
        Boolean canBeBonus,
        Boolean active,
        String description,
        Integer sortOrder) {}
