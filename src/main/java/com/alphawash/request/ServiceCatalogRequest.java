package com.alphawash.request;

import com.alphawash.constant.Size;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalogRequest {
    private Size size;
    private BigDecimal price;
    private Long serviceId;
}
