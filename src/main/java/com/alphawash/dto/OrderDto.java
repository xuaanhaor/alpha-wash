package com.alphawash.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class OrderDto {
    private UUID id;
    private CustomerDto customer;
    private LocalDateTime date;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private String paymentStatus;
    private String paymentType;
    private BigDecimal tip;
    private BigDecimal vat;
    private BigDecimal discount;
    private String note;
    private BigDecimal totalPrice;
}
