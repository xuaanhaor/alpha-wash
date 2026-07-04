package com.alphawash.service;

import com.alphawash.dto.SupplierDto;
import java.util.List;

public interface SupplierService {
    List<SupplierDto> getAll();

    List<SupplierDto> getActive();

    SupplierDto getByCode(String code);

    SupplierDto create(SupplierDto dto);

    SupplierDto update(String code, SupplierDto dto);

    void delete(String code);
}
