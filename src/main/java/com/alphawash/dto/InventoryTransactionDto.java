package com.alphawash.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionDto {
    private Long id;
    private String code;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Integer beforeQty;
    private Integer afterQty;
    private String type;
    private String referenceNumber;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
}
