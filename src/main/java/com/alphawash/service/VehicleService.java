package com.alphawash.service;

import com.alphawash.dto.BasicVehicleServiceUsedSearchDto;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.response.BasicCustomerVehicleDetailResponse;
import java.util.List;
import java.util.UUID;

public interface VehicleService {
    List<VehicleDto> search();

    VehicleDto create(VehicleDto dto);

    VehicleDto findById(UUID id);

    VehicleDto update(VehicleDto request);

    VehicleDto findByLicensePlate(String licensePlate);

    List<CarSizeDto> getCarSizes();

    CarSizeDto updateCarSize(BasicCarSizeRequest request);

    List<BasicVehicleServiceUsedSearchDto> searchVehicleServiceUsage();

    BasicCustomerVehicleDetailResponse searchVehicleServiceUsageDetail(UUID customerId);
}
