package com.alphawash.request;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderItemRequest {
    private String productCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal adjustedPrice;
    private Boolean adjustedPriceFlag;
    private String adjustedPriceReason;
    private BigDecimal discount;
    private String note;
}
