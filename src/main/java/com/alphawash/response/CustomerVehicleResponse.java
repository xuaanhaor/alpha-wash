package com.alphawash.response;

import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerVehicleResponse {
    private String phone;
    private UUID customerId;
    private String customerName;
    private List<VehicleResponse> vehicles;
}
