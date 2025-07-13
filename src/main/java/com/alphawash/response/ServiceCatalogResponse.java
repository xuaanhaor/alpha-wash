package com.alphawash.response;

import com.alphawash.constant.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalogResponse {
    private Long id;
    private Size size;
    private BigDecimal price;
    private Long serviceId;
}
