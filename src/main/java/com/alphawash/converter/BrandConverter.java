package com.alphawash.converter;

import com.alphawash.dto.BrandDto;
import com.alphawash.entity.Brand;
import com.alphawash.request.BrandRequest;
import com.alphawash.response.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BrandConverter {
    BrandConverter INSTANCE = Mappers.getMapper(BrandConverter.class);

    @Mapping(target = "id", ignore = true)
    BrandDto fromRequest(BrandRequest request);
    BrandResponse toResponse(BrandDto dto);

    BrandDto toDto(Brand entity);
    Brand toEntity(BrandDto dto);

    List<BrandDto> toDto(List<Brand> brands);
    List<BrandResponse> toResponse(List<BrandDto> dtos);
}
