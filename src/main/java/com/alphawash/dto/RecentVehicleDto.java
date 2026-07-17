package com.alphawash.dto;

import java.time.LocalDateTime;
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
public class RecentVehicleDto {
    private String vehicleId;
    private String licensePlate;
    private String imageUrl;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String brandCode;
    private String brandName;
    private String modelCode;
    private String modelName;
    private String vehicleSize;
    private LocalDateTime lastOrderDate;
}
