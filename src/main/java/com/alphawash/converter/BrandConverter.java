package com.alphawash.converter;

import com.alphawash.dto.BrandDto;
import com.alphawash.entity.Brand;
import com.alphawash.request.BrandRequest;
import com.alphawash.response.BrandResponse;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandConverter {
    BrandConverter INSTANCE = Mappers.getMapper(BrandConverter.class);

    // ===== Request -> DTO =====
    @Mapping(target = "brandId", ignore = true)
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brandName")
    BrandDto fromRequest(BrandRequest request);

    // ===== DTO -> Response =====
    BrandResponse toResponse(BrandDto dto);

    // ===== Entity -> DTO =====
    @Mapping(target = "brandId", source = "id")
    @Mapping(target = "brandCode", source = "code")
    @Mapping(target = "brandName", source = "brandName")
    BrandDto toDto(Brand entity);

    // ===== DTO -> Entity =====
    @Mapping(target = "id", source = "brandId")
    @Mapping(target = "code", source = "brandCode")
    @Mapping(target = "brandName", source = "brandName")
    Brand toEntity(BrandDto dto);

    // ===== List mapping =====
    default List<BrandDto> toDto(List<Brand> brands) {
        if (brands == null) {
            return Collections.emptyList();
        }
        return brands.stream()
                .map(this::toDto)
                .toList(); // Java 16+
    }

    default List<BrandResponse> toResponse(List<BrandDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toResponse)
                .toList();
    }
}
