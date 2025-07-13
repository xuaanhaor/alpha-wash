package com.alphawash.response;

import com.alphawash.dto.VehicleDto;
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
    private List<VehicleDto> vehicles;
}
