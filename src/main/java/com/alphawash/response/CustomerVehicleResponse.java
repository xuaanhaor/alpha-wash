package com.alphawash.response;

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
}
