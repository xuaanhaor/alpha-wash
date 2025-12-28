package com.alphawash.service;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import java.util.List;

public interface BrandService {
    List<BrandDto> getAll();

    BrandDto upsert(BrandDto dto);

    boolean delete(String code);

    List<BrandWithModelDto> getBrandWithModel();

    BrandWithModelDto getBrandWithModelByBrandCode(String code);
}
