package com.alphawash.response;

import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.VehicleDto;
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
public class VehiclePlateCheckResponse {
    private boolean exists;
    private VehicleDto vehicle;
    private CustomerDto customer;
    private boolean hasCustomer;
}
