package com.alphawash.response;

import com.alphawash.dto.CustomerSegmentDto.SegmentBadge;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class CustomerListItemResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String avatarUrl;
    private List<VehicleRefResponse> vehicles;
    private int vehicleCount;
    private int totalVisits;
    private BigDecimal totalSpending;
    private LocalDateTime lastVisitDate;
    private List<SegmentBadge> tags;
    private String status;
    private LocalDateTime createdAt;
}
