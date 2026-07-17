package com.alphawash.service;

import com.alphawash.dto.BasicVehicleServiceUsedSearchDto;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.request.MergeVehiclesRequest;
import com.alphawash.request.VehicleRequest;
import com.alphawash.response.BasicCustomerVehicleDetailResponse;
import com.alphawash.response.DuplicateVehicleGroupResponse;
import com.alphawash.response.MergeVehicleLogResponse;
import com.alphawash.response.VehicleMergePreviewResponse;
import com.alphawash.response.VehicleMergeResponse;
import com.alphawash.response.VehiclePlateCheckResponse;
import java.util.List;
import java.util.UUID;

public interface VehicleService {
    List<VehicleDto> search();

    VehicleDto insert(VehicleRequest request);

    VehicleDto findById(UUID id);

    void update(VehicleRequest request);

    VehicleDto findByLicensePlate(String licensePlate);

    List<CarSizeDto> getCarSizes();

    CarSizeDto updateCarSize(BasicCarSizeRequest request);

    List<BasicVehicleServiceUsedSearchDto> searchVehicleServiceUsage();

    BasicCustomerVehicleDetailResponse searchVehicleServiceUsageDetail(UUID customerId);

    VehiclePlateCheckResponse checkPlate(String plate);

    VehicleDto linkCustomer(UUID vehicleId, UUID customerId);

    VehicleDto transferOwnership(UUID vehicleId, UUID newCustomerId);

    List<DuplicateVehicleGroupResponse> findDuplicateVehicles();

    VehicleMergeResponse mergeVehicles(MergeVehiclesRequest request);

    VehicleMergePreviewResponse previewMerge(List<UUID> vehicleIds);

    List<MergeVehicleLogResponse> getMergeLogs();
}
