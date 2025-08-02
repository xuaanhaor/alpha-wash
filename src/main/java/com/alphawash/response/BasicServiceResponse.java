package com.alphawash.response;

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
public class BasicServiceResponse {
    private Long serviceId;
    private String serviceTypeCode;
    private String serviceTypeName;
    private String serviceCode;
    private String serviceName;
    private String serviceCatalogCode;
    private BigDecimal price;
    private String duration;
    private String size;
    private String note;
}
