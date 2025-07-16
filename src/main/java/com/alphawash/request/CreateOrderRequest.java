package com.alphawash.request;

import com.alphawash.dto.CustomerDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CreateOrderRequest(
        CustomerDto customer,
        LocalDateTime date,
        LocalTime checkinTime,
        LocalTime checkoutTime,
        String paymentStatus,
        String paymentType,
        BigDecimal tip,
        BigDecimal vat,
        BigDecimal discount,
        String note,
        BigDecimal totalPrice) {}
