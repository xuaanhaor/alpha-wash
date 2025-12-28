package com.alphawash.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboGetAllResponse {

    private String comboCode;
    private String comboName;
    private Integer durationDays;
    private String status;
    private List<Catalog> catalogs;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Catalog {
        private String catalogCode;
        private String size;
        private BigDecimal price;
        private Boolean priceIncludeTax;
        private List<ServiceQuota> services;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServiceQuota {
        private String serviceCatalogCode;
        private Integer quantity;
    }
}