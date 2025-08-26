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
public class BasicVehicleServiceUsedSearchDto {
    private Integer id;
    private String licensePlate;
    private String vehicleName;
    private String customerName;
    private UUID customerId;
    private String phone;
    private Integer serviceUsage;
    private String note;
}
