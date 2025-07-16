package com.alphawash.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BasicInformationRequest(
        LocalDateTime date,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String paymentType,
        String paymentStatus,
        BigDecimal tip,
        BigDecimal discount,
        BigDecimal vat,
        BigDecimal totalPrice,
        String employeeId,
        String note) {}
