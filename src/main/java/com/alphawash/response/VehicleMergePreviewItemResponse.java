package com.alphawash.response;

import com.alphawash.dto.CustomerDto;
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
public class VehicleMergePreviewItemResponse {
    private UUID vehicleId;
    private String licensePlate;
    private CustomerDto customer;
    private int orderCount;
    private BigDecimal totalSpending;
    private LocalDateTime lastVisitDate;
}
