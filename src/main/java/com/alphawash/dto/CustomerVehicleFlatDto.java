package com.alphawash.dto;

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
public class CustomerVehicleFlatDto {
    private UUID id;
    private String phone;
    private String customerName;
    private String brandCode;
    private String brandName;
    private String modelCode;
    private String modelName;
    private String licensePlate;
}
