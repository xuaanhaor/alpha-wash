package com.alphawash.response;

import com.alphawash.constant.ServiceCategory;
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
public class ServiceItemResponse {
    private UUID id;
    private String name;
    private ServiceCategory category;
    private String brand;
    private String typeDetail;
    private String warranty;
    private BigDecimal priceS;
    private BigDecimal priceM;
    private BigDecimal priceL;
    private BigDecimal priceSEDAN;
    private BigDecimal priceSUV;
    private BigDecimal priceOverSize;
    private boolean canBeBonus;
    private boolean active;
    private String description;
    private int sortOrder;
}
