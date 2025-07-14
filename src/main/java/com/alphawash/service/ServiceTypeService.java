package com.alphawash.service;

import com.alphawash.dto.ServiceTypeDto;
import java.util.List;

public interface ServiceTypeService {
    List<ServiceTypeDto> getAll();

    ServiceTypeDto getById(Long id);

    ServiceTypeDto create(ServiceTypeDto dto);

    ServiceTypeDto update(Long id, ServiceTypeDto dto);

    void delete(Long id);
}
