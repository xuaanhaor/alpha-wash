package com.alphawash.request;

import java.math.BigDecimal;
import java.util.List;

public record CreateBasicComboRequest(
        String comboName, BigDecimal price, String note, List<String> serviceCatalogCodes) {}
