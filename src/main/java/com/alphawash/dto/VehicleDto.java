package com.alphawash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private UUID vehicleId;
    private UUID customerId;
    private String licensePlate;
    private BrandDto brand;
    private ModelDto model;
    private String imageUrl;
    private String note;
}
