package com.alphawash.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemDto {
    private Long id;
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Integer receivedQuantity;
}
