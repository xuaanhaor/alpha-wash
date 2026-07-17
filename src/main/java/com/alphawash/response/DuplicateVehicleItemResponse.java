package com.alphawash.response;

import com.alphawash.dto.CustomerDto;
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
public class DuplicateVehicleItemResponse {
    private UUID id;
    private String licensePlate;
    private CustomerDto customer;
    private int orderCount;
    private LocalDateTime lastServiceDate;
}
