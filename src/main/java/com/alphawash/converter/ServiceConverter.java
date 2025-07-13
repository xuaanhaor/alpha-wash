package com.alphawash.converter;

import com.alphawash.dto.ServiceDto;
import com.alphawash.entity.Service;
import com.alphawash.request.ServiceRequest;
import com.alphawash.response.ServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueMappingStrategy =  NullValueMappingStrategy.RETURN_DEFAULT)
public interface ServiceConverter {
    ServiceConverter INSTANCE = Mappers.getMapper(ServiceConverter.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceTypeId", source = "serviceTypeId")
    ServiceDto fromRequest(ServiceRequest request);

    @Mapping(target = "serviceTypeId", source = "serviceType.id")
    ServiceDto toDto(Service entity);

    @Mapping(target = "serviceType", ignore = true)
    Service toEntity(ServiceDto dto);

    ServiceResponse toResponse(ServiceDto dto);
    List<ServiceDto> toDto(List<Service> entities);
    List<ServiceResponse> toResponse(List<ServiceDto> dtos);
}
