package com.alphawash.converter;

import com.alphawash.dto.VehicleDto;
import com.alphawash.entity.Vehicle;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VehicleConverter {
    VehicleConverter INSTANCE = Mappers.getMapper(VehicleConverter.class);
    List<VehicleDto> toDto(List<Vehicle> vehicles);
}
