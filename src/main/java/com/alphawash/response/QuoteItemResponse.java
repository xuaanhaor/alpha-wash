package com.alphawash.response;

import java.math.BigDecimal;
import java.util.UUID;
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
public class QuoteItemResponse {
    private UUID id;
    private UUID serviceId;
    private String serviceName;
    private String brand;
    private String typeDetail;
    private String warranty;
    private BigDecimal price;
    private boolean bonus;
    private int sortOrder;
}
