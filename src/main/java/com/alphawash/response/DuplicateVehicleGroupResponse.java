package com.alphawash.response;

import java.util.List;
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
public class DuplicateVehicleGroupResponse {
    private String normalizedPlate;
    private List<DuplicateVehicleItemResponse> vehicles;
}
