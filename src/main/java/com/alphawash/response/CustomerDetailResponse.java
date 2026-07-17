package com.alphawash.response;

import com.alphawash.constant.CustomerGender;
import com.alphawash.constant.CustomerStatus;
import com.alphawash.dto.CustomerSegmentDto.SegmentBadge;
import java.time.LocalDate;
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
public class CustomerDetailResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private CustomerGender gender;
    private LocalDate birthday;
    private String address;
    private String note;
    private String avatarUrl;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private List<VehicleSummaryResponse> vehicles;
    private List<SegmentBadge> tags;
    private CustomerStatsResponse stats;
}
