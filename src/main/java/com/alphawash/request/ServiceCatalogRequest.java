package com.alphawash.request;

import com.alphawash.constant.Size;

public record ServiceCatalogRequest(Long serviceId, String code, Double price, Size size) {}
