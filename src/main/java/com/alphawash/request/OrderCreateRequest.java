package com.alphawash.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        UUID customerId,
        String licensePlate,
        String brandCode,
        String modelCode,
        String imageUrl,
        String vehicleNote,
        LocalDateTime date,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String paymentType,
        String paymentStatus,
        BigDecimal tip,
        BigDecimal vat,
        String promotionId,
        BigDecimal discount,
        BigDecimal totalPrice,
        String note,
        List<OrderDetailRequest> orderDetails) {
    public record OrderDetailRequest(
            List<Long> employeeIds,
            String status,
            String note,
            String orderType,
            List<ServiceCreateRequest> services) {}

    public record ServiceCreateRequest(
            String serviceCatalogCode,
            String serviceComboCatalogCode,
            BigDecimal adjustedPrice,
            Boolean adjustedPriceFlag,
            String adjustedPriceReason) {}
}
