package com.alphawash.response;

import java.time.LocalDateTime;
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
public class MergeVehicleLogResponse {
    private Long id;
    private LocalDateTime mergeDate;
    private String operatorUsername;
    private UUID primaryCustomerId;
    private String primaryCustomerName;
    private UUID primaryVehicleId;
    private String primaryVehicleLicensePlate;
    private String duplicateCustomerIds;
    private String duplicateCustomerNames;
    private String duplicateVehicleLicensePlates;
    private Integer ordersMigrated;
    private Integer orderDetailsMigrated;
    private Integer customersArchived;
    private String status;
}
