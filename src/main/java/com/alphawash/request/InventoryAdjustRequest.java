package com.alphawash.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustRequest {
    private String productCode;
    private Integer quantity;
    private String type;
    private String referenceNumber;
    private String notes;
}
