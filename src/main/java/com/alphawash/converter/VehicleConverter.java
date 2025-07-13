package com.alphawash.converter;

import com.alphawash.dto.VehicleDto;
import com.alphawash.entity.Vehicle;
import com.alphawash.response.VehicleResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VehicleConverter {
    VehicleConverter INSTANCE = Mappers.getMapper(VehicleConverter.class);

    @Mapping(target = "id", ignore = true)
    Vehicle toVehicleDto(VehicleDto vehicleDto);

    VehicleResponse toResponse(VehicleDto vehicleDto);

    List<VehicleResponse> toResponse(List<VehicleDto> vehicleDtos);

    List<VehicleDto> toDto(List<Vehicle> vehicles);
}
