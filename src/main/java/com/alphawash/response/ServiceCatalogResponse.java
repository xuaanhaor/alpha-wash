package com.alphawash.response;

import com.alphawash.constant.Size;
import java.math.BigDecimal;
import lombok.*;

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
