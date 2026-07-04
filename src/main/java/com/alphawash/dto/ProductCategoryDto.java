package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryDto {
    private Long id;
    private String code;
    private String categoryName;
    private String color;
    private Integer displayOrder;
    private Boolean isActive;
    private Integer exclusiveKey;
}
