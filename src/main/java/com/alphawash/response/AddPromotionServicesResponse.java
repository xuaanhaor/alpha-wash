package com.alphawash.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPromotionServicesResponse {
    String serviceCode;
    String serviceName;
    BigDecimal discountAmount;
    BigDecimal discountPercent;
}
