package com.alphawash.response;

import com.alphawash.constant.QuoteStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteResponse {
    private UUID id;
    private String quoteCode;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private UUID vehicleId;
    private String licensePlate;
    private String carModel;
    private String carSize;
    private LocalDate quoteDate;
    private QuoteStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal extraCharge;
    private BigDecimal total;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<QuoteItemResponse> items;
}
