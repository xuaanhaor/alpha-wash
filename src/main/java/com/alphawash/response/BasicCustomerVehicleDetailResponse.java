package com.alphawash.response;

import com.alphawash.dto.BasicVehicleServiceUsedDto;
import java.util.List;
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
public class BasicCustomerVehicleDetailResponse {
    private UUID customerId;
    private String customerName;
    private String phone;
    private List<BasicVehicleServiceUsedDto> vehicles;
}
