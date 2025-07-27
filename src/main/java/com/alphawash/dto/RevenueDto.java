package com.alphawash.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class RevenueDto {
    private LocalDate orderDate;
    private String serviceTypeCode;
    private String serviceName;
    private String serviceTypeName;
    private BigDecimal netRevenue;
    private BigDecimal grossRevenue;
}
