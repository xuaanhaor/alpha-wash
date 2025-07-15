package com.alphawash.converter;

import com.alphawash.dto.ServiceTypeDto;
import com.alphawash.entity.ServiceType;
import com.alphawash.request.ServiceTypeRequest;
import com.alphawash.response.ServiceTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceTypeConverter {
    ServiceTypeConverter INSTANCE = Mappers.getMapper(ServiceTypeConverter.class);

    @Mapping(target = "id", ignore = true)
    ServiceTypeDto fromRequest(ServiceTypeRequest request);

    ServiceType toEntity(ServiceTypeDto dto);

    ServiceTypeDto toDto(ServiceType entity);

    ServiceTypeResponse toResponse(ServiceTypeDto dto);

    List<ServiceTypeDto> toDto(List<ServiceType> entities);

    List<ServiceTypeResponse> toResponse(List<ServiceTypeDto> dtos);
}
