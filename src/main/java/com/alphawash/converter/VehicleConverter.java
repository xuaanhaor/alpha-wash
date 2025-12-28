package com.alphawash.converter;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.ModelDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.entity.Vehicle;
import com.alphawash.request.VehicleRequest;

import java.util.List;
import java.util.stream.Collectors;

public final class VehicleConverter {

    private VehicleConverter() {}

    // Request -> DTO
    public static VehicleDto fromRequest(VehicleRequest req) {
        if (req == null) return null;

        return VehicleDto.builder()
                .vehicleId(req.vehicleId())
                .customerId(req.customerId())
                .licensePlate(req.licensePlate())
                .brand(req.brand())
                .model(req.model())
                .imageUrl(req.imageUrl())
                .note(req.note())
                .build();
    }

    // Entity -> DTO
    public static VehicleDto toDto(Vehicle v) {
        if (v == null) return null;

        BrandDto brandDto = null;
        if (v.getBrand() != null) {
            brandDto = BrandDto.builder()
                    .brandId(v.getBrand().getId())
                    .brandCode(v.getBrand().getCode())
                    .brandName(v.getBrand().getBrandName())
                    .build();
        }

        ModelDto modelDto = null;
        if (v.getModel() != null) {
            modelDto = ModelDto.builder()
                    .modelId(v.getModel().getId())
                    .modelCode(v.getModel().getCode())
                    .modelName(v.getModel().getModelName())
                    .size(v.getModel().getSize() != null ? v.getModel().getSize().name() : null)
                    .brandCode(v.getModel().getBrand() != null ? v.getModel().getBrand().getCode() : null)
                    .brandName(v.getModel().getBrand() != null ? v.getModel().getBrand().getBrandName() : null)
                    .note(v.getModel().getNote())
                    .build();
        }

        return VehicleDto.builder()
                .vehicleId(v.getId())
                .customerId(v.getCustomer() != null ? v.getCustomer().getId() : null)
                .licensePlate(v.getLicensePlate())
                .brand(brandDto)
                .model(modelDto)
                .imageUrl(v.getImageUrl())
                .note(v.getNote())
                .build();
    }

    public static List<VehicleDto> toDtoList(List<Vehicle> vehicles) {
        if (vehicles == null) return List.of();
        return vehicles.stream().map(VehicleConverter::toDto).collect(Collectors.toList());
    }


}
