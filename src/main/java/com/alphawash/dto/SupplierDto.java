package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {
    private Long id;
    private String code;
    private String supplierName;
    private String phone;
    private String email;
    private String address;
    private String taxId;
    private String notes;
    private Boolean isActive;
    private Integer exclusiveKey;
}
