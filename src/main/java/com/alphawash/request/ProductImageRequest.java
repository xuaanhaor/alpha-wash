package com.alphawash.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isPrimary;
}
