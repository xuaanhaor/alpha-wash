package com.alphawash.service;

import com.alphawash.dto.ModelDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import java.util.List;

public interface ModelService {
    ModelDto create(ModelDto dto);

    ModelDto update(ModelDto dto);

    boolean delete(String code);

    //Dành cho tạo mới hóa đơn
    List<ModelWithoutBrandDto> findByBrandCode(String brandCode);
}
