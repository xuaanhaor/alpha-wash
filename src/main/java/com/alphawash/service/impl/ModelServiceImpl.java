package com.alphawash.service.impl;

import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.ModelDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Model;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.service.ModelService;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
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
    public ModelDto update(Long id, ModelDto patchData) {
        return modelRepository
                .findById(id)
                .map(existing -> {
                    ModelDto currentDto = converter.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    Model updatedEntity = converter.toEntity(currentDto);
                    if (ObjectUtils.isNotNull(currentDto.getBrandId())) {
                        updatedEntity.setBrand(brandRepository
                                .findById(currentDto.getBrandId())
                                .orElse(null));
                    }
                    return converter.toDto(modelRepository.save(updatedEntity));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        modelRepository.deleteById(id);
    }

    @Override
    public List<ModelWithoutBrandDto> findByBrandCode(String brandCode) {
        var list = modelRepository.findByBrandCode(brandCode);
        return list.stream()
                .map(model -> new ModelWithoutBrandDto(model.getId(), model.getCode(), model.getModelName(), model.getSize()))
                .toList();
    }
}
