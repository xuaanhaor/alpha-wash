package com.alphawash.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record OrderUpdateRequest(
        UUID orderId,
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
        List<OrderDetailUpdateRequest> orderDetails) {
    public record OrderDetailUpdateRequest(
            String orderDetailCode,
            String status,
            String note,
            List<Long> employeeIds,
            List<ServiceUpdateRequest> services) {}

    public record ServiceUpdateRequest(
            String serviceCatalogCode,
            String serviceComboCatalogCode,
            BigDecimal adjustedPrice,
            Boolean adjustedPriceFlag,
            String adjustedPriceReason) {}
}
