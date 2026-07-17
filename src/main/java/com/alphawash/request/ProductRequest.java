package com.alphawash.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String code;
    private String barcode;
    private String productName;
    private String categoryCode;
    private String brand;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private BigDecimal minPrice;
    private BigDecimal suggestedPrice;
    private Integer currentStock;
    private Integer minStock;
    private String unit;
    private String location;
    private Boolean trackInventory;
    private String supplierCode;
    private String supplierSku;
    private Boolean isActive;
    private List<ProductImageRequest> images;
    private Integer exclusiveKey;
}
