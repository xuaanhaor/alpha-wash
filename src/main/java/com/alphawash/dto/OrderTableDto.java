package com.alphawash.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OrderTableDto {
    private UUID id;
    private Timestamp orderDate;
    private Time checkIn;
    private Time checkOut;

    private String paymentStatus;
    private String paymentType;
    private BigDecimal tip;
    private BigDecimal vat;
    private BigDecimal discount;
    private Boolean deleteFlag;
    private BigDecimal totalPrice;
    private String note;

    private CustomerDTO customer;

    private List<OrderDetailDTO> orderDetails;

    @Data
    public static class CustomerDTO {
        private UUID id;
        private String name;
        private String phone;
    }

    @Getter
    @Setter
    @Data
    public static class OrderDetailDTO {
        private List<EmployeeDTO> employees;
        private VehicleDTO vehicle;
        private ServiceDTO service;
        private String status;
        private String note;
    }

    @Data
    public static class EmployeeDTO {
        private Long id;
        private String name;
    }

    @Data
    public static class VehicleDTO {
        private UUID id;
        private String licensePlate;
        private Long brandId;
        private String brandName;
        private String brandCode;
        private Long modelId;
        private String modelName;
        private String modelCode;
        private String size;
        private String imageUrl;
    }

    @Data
    public static class ServiceDTO {
        private Long id;
        private String serviceCode;
        private String serviceName;
        private String serviceTypeCode;
        private ServiceCatalogDTO serviceCatalog;
    }

    @Data
    public static class ServiceCatalogDTO {
        private Long id;
        private String serviceCatalogCode;
        private BigDecimal price;
        private String size;
    }
}
