package com.alphawash.response;

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
public class VehicleSummaryResponse {
    private UUID id;
    private String licensePlate;
    private Long brandId;
    private String brandCode;
    private String brandName;
    private Long modelId;
    private String modelCode;
    private String modelName;
    private String size;
    private String imageUrl;
    private UUID customerId;
}
