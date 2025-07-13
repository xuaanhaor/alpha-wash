package com.alphawash.service;

import com.alphawash.dto.VehicleDto;
import com.alphawash.request.VehicleRequest;
import java.util.List;
import java.util.UUID;

public interface VehicleService {
    List<VehicleDto> search();

    List<VehicleDto> insert(VehicleRequest request);

    VehicleDto findById(UUID id);

    void update(VehicleRequest request);

    VehicleDto findByLicensePlate(String licensePlate);
}
