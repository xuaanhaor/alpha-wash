package com.alphawash.request;

import com.alphawash.constant.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalogRequest {
    private Size size;
    private BigDecimal price;
    private Long serviceId;
}
