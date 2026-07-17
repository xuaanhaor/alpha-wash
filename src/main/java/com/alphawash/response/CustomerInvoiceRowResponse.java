package com.alphawash.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class CustomerInvoiceRowResponse {
    private UUID id;
    private String code;
    private LocalDateTime date;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String paymentType;
    private String licensePlate;
}
