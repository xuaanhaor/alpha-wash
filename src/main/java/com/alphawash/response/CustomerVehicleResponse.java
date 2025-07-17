package com.alphawash.response;

import com.alphawash.dto.CustomerVehicleDto;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerVehicleResponse {
    private UUID customerId;
    private String customerName;
    private String phone;
    List<CustomerVehicleDto> vehicles;
}
