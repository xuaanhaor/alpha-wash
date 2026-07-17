package com.alphawash.request;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemRequest {
    private String productCode;
    private Integer quantity;
    private BigDecimal unitCost;
}
