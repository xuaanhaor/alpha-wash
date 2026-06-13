package com.alphawash.dto;

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
public class VehicleServiceUsedDto {
    private UUID customerId;
    private String customerName;
    private String phone;
    private String vehicleName;
    private String licensePlate;
    private String serviceName;
    private LocalTime checkinTime;
}
