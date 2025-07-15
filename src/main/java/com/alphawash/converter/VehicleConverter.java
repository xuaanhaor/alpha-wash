package com.alphawash.converter;

import com.alphawash.dto.VehicleDto;
import com.alphawash.entity.Vehicle;
import com.alphawash.request.VehicleRequest;
import com.alphawash.response.VehicleResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VehicleConverter {
    VehicleConverter INSTANCE = Mappers.getMapper(VehicleConverter.class);

    VehicleResponse toResponse(VehicleDto vehicleDto);

    List<VehicleResponse> toResponse(List<VehicleDto> vehicleDtos);

    VehicleDto toDto(Vehicle vehicle);

    List<VehicleDto> toDto(List<Vehicle> vehicles);

    Vehicle fromRequest(VehicleRequest request);
}
