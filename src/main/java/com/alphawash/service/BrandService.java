package com.alphawash.service;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import java.util.List;

public interface BrandService {
    List<BrandDto> getAll();

    BrandDto getById(Long id);

    BrandDto create(BrandDto dto);

    BrandDto update(Long id, BrandDto dto);

    void delete(Long id);

    List<BrandWithModelDto> getBrandWithModel();

    BrandWithModelDto getBrandWithModelByBrandCode(String code);
}
