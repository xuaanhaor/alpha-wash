package com.alphawash.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelFromEntityDto {
    private Long id;
    private String code;
    private String modelName;
    private BrandDto brand;
    private String size;
    private String note;
}
