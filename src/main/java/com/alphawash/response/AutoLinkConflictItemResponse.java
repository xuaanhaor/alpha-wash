package com.alphawash.response;

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
public class AutoLinkConflictItemResponse {
    private UUID vehicleId;
    private String licensePlate;
    private List<AutoLinkCustomerOptionResponse> customerOptions;
}
