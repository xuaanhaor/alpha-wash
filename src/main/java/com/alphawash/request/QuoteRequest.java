package com.alphawash.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteRequest(
        UUID customerId,
        String customerName,
        String customerPhone,
        UUID vehicleId,
        String licensePlate,
        String carModel,
        String carSize,
        LocalDate quoteDate,
        BigDecimal discount,
        BigDecimal extraCharge,
        String notes,
        List<QuoteItemRequest> items) {}
