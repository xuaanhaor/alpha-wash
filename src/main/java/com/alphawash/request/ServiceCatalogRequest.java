package com.alphawash.request;

import com.alphawash.constant.Size;
import java.math.BigDecimal;

public record ServiceCatalogRequest(Long serviceId, String code, Double price, Size size, BigDecimal tempPrice) {}
