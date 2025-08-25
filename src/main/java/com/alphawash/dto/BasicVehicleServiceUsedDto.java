package com.alphawash.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicVehicleServiceUsedDto {
    private String vehicleName;
    private String licensePlate;
    private List<VehicleServicesDto> services;
}
