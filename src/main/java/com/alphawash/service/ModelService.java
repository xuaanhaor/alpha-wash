package com.alphawash.service;

import com.alphawash.dto.ModelDto;

import java.util.List;

public interface ModelService {
    List<ModelDto> getAll();
    ModelDto getById(Long id);
    ModelDto create(ModelDto dto);
    ModelDto update(Long id, ModelDto dto);
    void delete(Long id);
}
