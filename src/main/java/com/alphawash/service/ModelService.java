package com.alphawash.service;

import com.alphawash.dto.ModelDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import java.util.List;

public interface ModelService {
    List<ModelDto> getAll();

    ModelDto getById(Long id);

    ModelDto create(ModelDto dto);

    ModelDto update(Long id, ModelDto dto);

    void delete(Long id);

    List<ModelWithoutBrandDto> findByBrandCode(String brandCode);
}
