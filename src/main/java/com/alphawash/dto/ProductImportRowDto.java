package com.alphawash.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImportRowDto {
    private Integer rowNumber;
    private String productCode;
    private String productName;
    private String categoryName;
    private String brand;
    private String description;
    private String unit;
    private String costPrice;
    private String sellingPrice;
    private String minPrice;
    private String barcode;
    private String currentStock;
    private String minStock;
    private String location;
    private String supplierName;
    private String status;
    private boolean valid;
    private List<String> errors;
}
