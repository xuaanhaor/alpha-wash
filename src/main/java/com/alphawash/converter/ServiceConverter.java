package com.alphawash.converter;

import com.alphawash.dto.ServiceDto;
import com.alphawash.entity.Service;
import com.alphawash.request.ServiceRequest;
import com.alphawash.response.ServiceResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ServiceConverter {
    ServiceConverter INSTANCE = Mappers.getMapper(ServiceConverter.class);

    @Mapping(target = "id", ignore = true)
    ServiceDto fromRequest(ServiceRequest request);

    @Mapping(target = "serviceTypeCode", source = "serviceType.code")
    ServiceDto toDto(Service entity);

    @Mapping(target = "serviceType", ignore = true)
    Service toEntity(ServiceDto dto);

    ServiceResponse toResponse(ServiceDto dto);

    List<ServiceDto> toDto(List<Service> entities);

    List<ServiceResponse> toResponse(List<ServiceDto> dtos);
}
