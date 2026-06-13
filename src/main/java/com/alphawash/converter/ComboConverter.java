package com.alphawash.converter;

import com.alphawash.dto.ServiceComboDto;
import com.alphawash.entity.ServiceCombo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComboConverter {
    ComboConverter INSTANCE = Mappers.getMapper(ComboConverter.class);

    ServiceComboDto toDto(ServiceCombo combo);

    List<ServiceComboDto> toDto(List<ServiceCombo> combo);
}
