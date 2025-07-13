package com.alphawash.service.impl;

import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.VehicleDto;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.VehicleRequest;
import com.alphawash.service.VehicleService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;

    @Override
    public List<VehicleDto> search() {
        var result = repository.findAll();
        return VehicleConverter.INSTANCE.toDto(result);
    }

    @Override
    @Transactional
    public List<VehicleDto> insert(VehicleRequest request) {
        var dto = repository.findById(request.id());
        if (dto.isPresent()) {
            //            Object vehicle = null;
            //            repository.save(vehicle);
            //            return VehicleConverter.INSTANCE.toDto(List.of(vehicle));
        }
        return null;
    }

    @Override
    public VehicleDto findById(UUID id) {
        var dto = repository.findById(id);
        return null;
    }

    @Override
    @Transactional
    public void update(VehicleRequest request) {
        var dto = repository.findById(request.id());
        dto.ifPresentOrElse(
                vehicle -> {
                    //                    var updatedVehicle = VehicleConverter.INSTANCE.fromRequest(request);
                    //                    updatedVehicle.setId(vehicle.getId());
                    //                    repository.save(updatedVehicle);
                },
                () -> {
                    throw new IllegalArgumentException("Vehicle not found with id: " + request.id());
                });
    }

    @Override
    public VehicleDto findByLicensePlate(String licensePlate) {
        var vehicle = repository.findByLicensePlate(licensePlate);
        //        return vehicle.map(VehicleConverter.INSTANCE::toDto).orElse(null);
        return null; // Placeholder for actual implementation
    }
}
