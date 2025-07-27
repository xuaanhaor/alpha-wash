package com.alphawash.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteServiceRevenueDto {
    private String serviceCode;
    private String serviceName;
    private Long usageCount;
    private BigDecimal totalRevenue;
}
