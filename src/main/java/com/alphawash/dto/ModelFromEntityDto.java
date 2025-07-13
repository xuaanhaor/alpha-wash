package com.alphawash.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelFromEntityDto {
    private Long id;
    private String modelName;
    private String size;
    private BrandDto brand;
}
