package com.alphawash.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderRequest {
    private String code;
    private String supplierCode;
    private LocalDateTime purchaseDate;
    private String invoiceNumber;
    private String status;
    private String notes;
    private List<PurchaseOrderItemRequest> items;
    private Integer exclusiveKey;
}
