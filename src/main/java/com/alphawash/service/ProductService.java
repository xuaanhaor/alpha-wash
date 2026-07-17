package com.alphawash.service;

import com.alphawash.dto.ProductDto;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAll();

    List<ProductDto> getActive();

    ProductDto getByCode(String code);

    ProductDto getByBarcode(String barcode);

    List<ProductDto> getByCategoryCode(String categoryCode);

    ProductDto create(ProductDto dto);

    ProductDto update(String code, ProductDto dto);

    void delete(String code);
}
