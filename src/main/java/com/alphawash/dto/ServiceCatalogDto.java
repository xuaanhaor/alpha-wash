package com.alphawash.dto;

import com.alphawash.constant.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalogDto {
    private Long id;
    private Size size;
    private BigDecimal price;
    private Long serviceId;
}
