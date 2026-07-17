package com.alphawash.service;

import com.alphawash.dto.ProductCategoryDto;
import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryDto> getAll();

    List<ProductCategoryDto> getActive();

    ProductCategoryDto getByCode(String code);

    ProductCategoryDto create(ProductCategoryDto dto);

    ProductCategoryDto update(String code, ProductCategoryDto dto);

    void delete(String code);
}
