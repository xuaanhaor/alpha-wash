package com.alphawash.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelResponse {
    private Long id;
    private String modelName;
    private String size;
    private Long brandId;
}
