package com.alphawash.dto;

import com.alphawash.entity.Order;
import java.util.UUID;
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
public class OrderDetailDto {
    private UUID id;
    private Order order;
    private String employeeId;
    private ServiceCatalogDto serviceCatalog;
    private String status;
    private String note;
    private VehicleDto vehicle;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class ServiceCatalogDto {
        private Long id;
        private String code;
        private String size;
        private String price;
        private ServiceDto service;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class VehicleDto {
        private UUID id;
        private CustomerDto customer;
        private String licensePlate;
        private BrandDto brand;
        private ModelFromEntityDto model;
        private String imageUrl;
        private String note;
    }
}
