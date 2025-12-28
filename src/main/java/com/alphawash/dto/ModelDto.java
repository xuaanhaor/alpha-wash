package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDto {
    private Long modelId;
    private String modelCode;
    private String modelName;
    private String size;
    private String brandCode;
    private String brandName;
    private String note;
}
