package com.alphawash.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelRequest {
    private String code;
    private String modelName;
    private String size;
    private Long brandId;
    private String note;
}
