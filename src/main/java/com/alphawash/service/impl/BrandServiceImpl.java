package com.alphawash.service.impl;

import com.alphawash.converter.BrandConverter;
import com.alphawash.converter.BrandWithModelConverter;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import com.alphawash.entity.Brand;
import com.alphawash.repository.BrandRepository;
import com.alphawash.service.BrandService;
import com.alphawash.util.PatchHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandConverter converter = BrandConverter.INSTANCE;

    @Override
    public List<BrandDto> getAll() {
        return converter.toDto(brandRepository.findAll());
    }

    @Override
    public BrandDto getById(Long id) {
        return brandRepository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public BrandDto create(BrandDto dto) {
        Brand saved = brandRepository.save(converter.toEntity(dto));
        return converter.toDto(saved);
    }

    @Override
    public BrandDto update(Long id, BrandDto patchData) {
        return brandRepository
                .findById(id)
                .map(existing -> {
                    BrandDto currentDto = converter.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    Brand updated = converter.toEntity(currentDto);
                    return converter.toDto(brandRepository.save(updated));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public List<BrandWithModelDto> getBrandWithModel() {
        List<Object[]> rows = brandRepository.getBrandWithModel();
        return BrandWithModelConverter.mapList(rows);
    }

    @Override
    public BrandWithModelDto getBrandWithModelByBrandCode(String code) {
        List<Object[]> row = brandRepository.findModelsByBrandId(code);
        return BrandWithModelConverter.map(row);
    }
}
