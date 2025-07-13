package com.alphawash.service;

import com.alphawash.dto.ServiceDto;

import java.util.List;

public interface ServiceService {
    List<ServiceDto> getAll();
    ServiceDto getById(Long id);
    ServiceDto create(ServiceDto dto);
    ServiceDto update(Long id, ServiceDto dto);
    void delete(Long id);
}
