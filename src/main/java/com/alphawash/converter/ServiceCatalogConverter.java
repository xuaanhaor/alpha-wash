package com.alphawash.converter;

import com.alphawash.dto.ServiceCatalogDto;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.request.ServiceCatalogRequest;
import com.alphawash.response.ServiceCatalogResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceCatalogConverter {
    ServiceCatalogConverter INSTANCE = Mappers.getMapper(ServiceCatalogConverter.class);

    @Mapping(target = "id", ignore = true)
    ServiceCatalogDto fromRequest(ServiceCatalogRequest request);

    @Mapping(target = "serviceId", source = "service.id")
    ServiceCatalogDto toDto(ServiceCatalog entity);

    @Mapping(target = "service", ignore = true)
    ServiceCatalog toEntity(ServiceCatalogDto dto);

    ServiceCatalogResponse toResponse(ServiceCatalogDto dto);

    List<ServiceCatalogDto> toDto(List<ServiceCatalog> entities);

    List<ServiceCatalogResponse> toResponse(List<ServiceCatalogDto> dtos);
}
