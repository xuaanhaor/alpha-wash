package com.alphawash.service.impl;

import com.alphawash.constant.Size;
import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.entity.Model;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.request.VehicleRequest;
import com.alphawash.service.VehicleService;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;

    @Override
    public List<VehicleDto> search() {
        var result = repository.findAll();
        return VehicleConverter.INSTANCE.toDto(result);
    }

    @Override
    @Transactional
    public VehicleDto insert(VehicleRequest request) {
        var dto = repository.findById(request.id());
        if (dto.isEmpty()) {
            var savedVehicle = repository.save(VehicleConverter.INSTANCE.fromRequest(request));
            return VehicleConverter.INSTANCE.toDto(savedVehicle);
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
                    var updatedVehicle = VehicleConverter.INSTANCE.fromRequest(request);
                    updatedVehicle.setId(vehicle.getId());
                    repository.save(updatedVehicle);
                },
                () -> {
                    throw new IllegalArgumentException("Vehicle not found with id: " + request.id());
                });
    }

    @Override
    public VehicleDto findByLicensePlate(String licensePlate) {
        var vehicle = repository.findByLicensePlate(licensePlate);
        if (vehicle.isPresent()) {
            return VehicleConverter.INSTANCE.toDto(vehicle.get());
        } else {
            throw new IllegalArgumentException("Vehicle not found with license plate: " + licensePlate);
        }
    }

    @Override
    public List<CarSizeDto> getCarSizes() {
        return repository.findCar().stream()
                .map(row -> {
                    CarSizeDto dto = new CarSizeDto();
                    dto.setBrandCode((String) row[0]);
                    dto.setModelCode((String) row[1]);
                    dto.setBrandName((String) row[2]);
                    dto.setModelName((String) row[3]);
                    dto.setSize(Size.valueOf((String) row[4]));
                    dto.setNote((String) row[5]);
                    return dto;
                })
                .toList();
    }

    @Override
    public CarSizeDto updateCarSize(BasicCarSizeRequest request) {
        String modelCode = request.modelCode();
        Model model;

        if (StringUtils.isNullOrBlank(request.modelCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Mã model không được để rỗng!");
        } else {
            model = modelRepository
                    .findByCode(modelCode)
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Model xe không tồn tại với mã: " + modelCode));
        }

        Size size = Size.fromString(request.size());
        model.setSize(size);
        ObjectUtils.setIfNotNull(request.note(), model::setNote);
        modelRepository.save(model);
        return CarSizeDto.builder()
                .brandCode(model.getBrand().getCode())
                .modelCode(model.getCode())
                .brandName(model.getBrand().getBrandName())
                .modelName(model.getModelName())
                .size(size)
                .note(model.getNote())
                .build();
    }
}
