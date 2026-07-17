package com.alphawash.response;

import java.math.BigDecimal;
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
public class VehicleMergePreviewResponse {
    private List<VehicleMergePreviewItemResponse> vehicles;
    private int totalOrders;
    private BigDecimal totalSpending;
}
