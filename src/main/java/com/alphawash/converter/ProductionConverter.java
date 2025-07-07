package com.alphawash.converter;

import com.alphawash.entity.Production;
import com.alphawash.request.ProductionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductionConverter {
    ProductionConverter INSTANCE = Mappers.getMapper(ProductionConverter.class);

    Production toEntity(ProductionRequest request);
}
