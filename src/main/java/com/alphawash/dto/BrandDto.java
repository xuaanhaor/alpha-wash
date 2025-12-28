package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDto {
    private Long brandId;
    private String brandCode;
    private String brandName;
}
