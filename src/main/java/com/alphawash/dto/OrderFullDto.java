package com.alphawash.dto;

import com.alphawash.constant.PromoType;
import com.alphawash.response.AddPromotionServicesResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderFullDto {
    private UUID id;
    private String code;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp date;

    private Time checkIn;
    private Time checkOut;
    private String paymentStatus;
    private String paymentType;
    private BigDecimal tip;
    private BigDecimal vat;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String note;
    private CustomerDTO customer;
    private Boolean deleteFlag;
    private List<OrderDetailDTO> orderDetails;
    private PromotionDTO promotion;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        private UUID id;
        private String name;
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailDTO {
        private String code;
        private List<EmployeeDTO> employees;
        private VehicleDTO vehicle;
        private List<ServiceDTO> service;
        private String status;
        private String note;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDTO {
        private Long id;
        private String serviceCode;
        private String serviceName;
        private String serviceTypeCode;
        private String adjustedPriceReason;
        private BigDecimal adjustedPrice;
        private Boolean adjustedPriceFlag;
        private ServiceCatalogDTO serviceCatalog;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceCatalogDTO {
        private Long id;
        private String code;
        private BigDecimal listedPrice;
        private String size;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromotionDTO {
        private UUID promoId;
        private String promoCode;
        private String promoName;
        private PromoType promoType;
        private BigDecimal value;
        private List<AddPromotionServicesResponse> services;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
