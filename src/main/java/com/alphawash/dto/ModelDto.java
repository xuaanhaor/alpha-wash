package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDto {
    private Long id;
    private String code;
    private String modelName;
    private String size;
    private Long brandId;
}
