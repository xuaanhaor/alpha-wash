package com.alphawash.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateServiceResponse {
    private String serviceCode;
    private String serviceName;
    private String duration;
    private String note;
    private List<CatalogInfo> catalogs;

    @Data
    @AllArgsConstructor
    public static class CatalogInfo {
        private String catalogCode;
        private String size;
        private BigDecimal price;
    }
}
