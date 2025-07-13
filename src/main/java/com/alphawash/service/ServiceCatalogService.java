package com.alphawash.service;

import com.alphawash.dto.ServiceCatalogDto;

import java.util.List;

public interface ServiceCatalogService {
    List<ServiceCatalogDto> getAll();
    ServiceCatalogDto getById(Long id);
    ServiceCatalogDto create(ServiceCatalogDto dto);
    ServiceCatalogDto update(Long id, ServiceCatalogDto dto);
    void delete(Long id);
}
