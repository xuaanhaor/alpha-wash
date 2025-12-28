package com.alphawash.converter;

import com.alphawash.constant.Size;
import com.alphawash.dto.ModelDto;
import com.alphawash.entity.Model;
import com.alphawash.request.ModelRequest;
import com.alphawash.response.ModelResponse;
import java.util.List;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelConverter {
    ModelConverter INSTANCE = Mappers.getMapper(ModelConverter.class);

    // =========================
    // Request -> DTO
    // =========================
    @Mapping(target = "modelId", ignore = true)
    @Mapping(target = "modelCode", ignore = true)   // code thường generate ở service
    @Mapping(target = "modelName", source = "modelName")
    @Mapping(target = "size", source = "size")      // nếu ModelRequest.size là String
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brandName")
    @Mapping(target = "note", source = "note")
    ModelDto fromRequest(ModelRequest request);

    // =========================
    // DTO -> Response
    // =========================
    ModelResponse toResponse(ModelDto dto);

    // =========================
    // Entity -> DTO
    // =========================
    @Mapping(target = "modelId", source = "id")
    @Mapping(target = "modelCode", source = "code")
    @Mapping(target = "modelName", source = "modelName")
    @Mapping(target = "size", source = "size") // Size -> String (convert bằng method dưới)
    @Mapping(target = "brandCode", source = "brand.code")
    @Mapping(target = "brandName", source = "brand.brandName")
    @Mapping(target = "note", source = "note")
    ModelDto toDto(Model entity);

    // =========================
    // DTO -> Entity
    // =========================
    @Mapping(target = "id", source = "modelId")
    @Mapping(target = "code", source = "modelCode")
    @Mapping(target = "modelName", source = "modelName")
    @Mapping(target = "size", source = "size") // String -> Size (convert bằng method dưới)
    @Mapping(target = "note", source = "note")
    @Mapping(target = "brand", ignore = true)  // set brand ở service bằng brandRepository
    Model toEntity(ModelDto dto);

    // =========================
    // List mapping
    // =========================
    List<ModelDto> toDto(List<Model> entities);

    List<ModelResponse> toResponse(List<ModelDto> dtos);

    // =========================
    // Converters: Size <-> String
    // =========================
    default String map(Size size) {
        return size == null ? null : size.name();
    }

    default Size map(String size) {
        if (size == null || size.isBlank()) return null;
        return Size.valueOf(size.trim().toUpperCase());
    }
}
