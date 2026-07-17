package com.alphawash.dto;

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
public class CustomerVehicleDto {
    private String brandCode;
    private String brandName;
    private String modelCode;
    private String modelName;
    private String licensePlate;
    private UUID id;
    private String size;
    private String imageUrl;
    private UUID customerId;
}
