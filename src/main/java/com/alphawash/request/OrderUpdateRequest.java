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
        String paymentStatus,
        String paymentType,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDateTime date,
        BigDecimal vat,
        BigDecimal tip,
        BigDecimal discount,
        BigDecimal totalPrice,
        String note,
        List<OrderDetailUpdateRequest> orderDetails) {
    public record OrderDetailUpdateRequest(
            String orderDetailCode,
            String status,
            String note,
            List<Long> employeeIds,
            List<ServiceUpdateRequest> services,
            List<ProductOrderItemRequest> products) {}

    public record ServiceUpdateRequest(
            String serviceCatalogCode,
            BigDecimal adjustedPrice,
            Boolean adjustedPriceFlag,
            String adjustedPriceReason,
            Integer quantity) {}
}
