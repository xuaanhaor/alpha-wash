package com.alphawash.service.impl;

import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.ModelDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Model;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.service.ModelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ModelConverter converter = ModelConverter.INSTANCE;

    @Override
    public List<ModelDto> getAll() {
        return converter.toDto(modelRepository.findAll());
    }

    @Override
    public ModelDto getById(Long id) {
        return modelRepository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public ModelDto create(ModelDto dto) {
        Brand brand = brandRepository.findById(dto.getBrandId()).orElseThrow();
        Model model = converter.toEntity(dto);
        model.setBrand(brand);
        return converter.toDto(modelRepository.save(model));
    }

    @Override
    public ModelDto update(Long id, ModelDto dto) {
        return modelRepository
                .findById(id)
                .map(existing -> {
                    existing.setModelName(dto.getModelName());
                    existing.setSize(dto.getSize());
                    existing.setBrand(brandRepository.findById(dto.getBrandId()).orElse(null));
                    return converter.toDto(modelRepository.save(existing));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        modelRepository.deleteById(id);
    }
}
