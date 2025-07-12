package com.alphawash.converter;

import com.alphawash.dto.ModelDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Model;
import com.alphawash.request.ModelRequest;
import com.alphawash.response.ModelResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ModelConverter {
    ModelConverter INSTANCE = Mappers.getMapper(ModelConverter.class);

    @Mapping(target = "id", ignore = true)
    ModelDto fromRequest(ModelRequest request);

    ModelResponse toResponse(ModelDto entity);

    @Mapping(target = "brandId", source = "brand.id")
    ModelDto toDto(Model entity);

    @Mapping(target = "brand", ignore = true)
    Model toEntity(ModelDto dto);

    List<ModelDto> toDto(List<Model> entities);
    List<ModelResponse> toResponse(List<ModelDto> dtos);
}
