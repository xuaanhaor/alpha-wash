package com.alphawash.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryRequest {
    private String code;
    private String categoryName;
    private String color;
    private Integer displayOrder;
    private Boolean isActive;
    private Integer exclusiveKey;
}
