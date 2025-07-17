package com.alphawash.response;

import com.alphawash.dto.OrderTableDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderServiceDetailResponse {
    private String id;
    private String order;
    private String employeeId;
    private OrderTableDto.ServiceCatalogDTO serviceCatalog;
    private String status;
    private String note;
    private String vehicle;
}
