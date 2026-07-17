package com.alphawash.dto;

import java.math.BigDecimal;
import java.util.List;
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
public class QuickServiceGroupDto {
    private String serviceTypeCode;
    private String serviceTypeName;
    private List<QuickServiceDto> services;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuickServiceDto {
        private String serviceCode;
        private String serviceName;
        private String duration;
        private List<QuickCatalogDto> catalogs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuickCatalogDto {
        private String catalogCode;
        private String size;
        private BigDecimal price;
    }
}
