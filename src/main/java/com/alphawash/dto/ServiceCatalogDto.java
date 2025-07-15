package com.alphawash.dto;

import com.alphawash.constant.Size;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalogDto {
    private Long id;
    private String code;
    private Size size;
    private BigDecimal price;
    private Long serviceId;
}
