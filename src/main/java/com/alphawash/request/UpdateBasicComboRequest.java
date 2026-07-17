package com.alphawash.request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateBasicComboRequest(
        String comboCode, String comboName, BigDecimal price, String note, List<String> serviceCatalogCodes) {}
