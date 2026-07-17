package com.alphawash.response;

import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.VehicleDto;
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
public class VehicleMergeResponse {
    private Long logId;
    private UUID primaryVehicleId;
    private List<UUID> archivedVehicleIds;
    private List<UUID> archivedCustomerIds;
    private int ordersMigrated;
    private int orderDetailsMigrated;
    private VehicleDto primaryVehicle;
    private CustomerDto primaryCustomer;
}
