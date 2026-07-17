package com.alphawash.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {
    private Long id;
    private String code;
    private String supplierCode;
    private String supplierName;
    private LocalDateTime purchaseDate;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private List<PurchaseOrderItemDto> items;
    private String createdBy;
    private LocalDateTime createdAt;
    private Integer exclusiveKey;
}
