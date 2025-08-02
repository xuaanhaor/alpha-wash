package com.alphawash.request;

import org.springframework.lang.Nullable;
import java.math.BigDecimal;

public record UpdateBasicServiceRequest(
        String serviceCode,
        String serviceName,
        BigDecimal price,
	String duration,
	String note,
        String size) {}
