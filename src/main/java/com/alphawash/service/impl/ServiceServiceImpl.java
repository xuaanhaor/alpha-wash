package com.alphawash.service.impl;

import com.alphawash.converter.ServiceConverter;
import com.alphawash.dto.ServiceDto;
import com.alphawash.entity.Service;
import com.alphawash.repository.ServiceRepository;
import com.alphawash.repository.ServiceTypeRepository;
import com.alphawash.service.ServiceService;
import com.alphawash.util.PatchHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceConverter converter = ServiceConverter.INSTANCE;

    @Override
    public List<ServiceDto> getAll() {
        return converter.toDto(serviceRepository.findAll());
    }

    @Override
    public ServiceDto getById(Long id) {
        return serviceRepository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public ServiceDto create(ServiceDto dto) {
        Service entity = converter.toEntity(dto);
        entity.setServiceType(
                serviceTypeRepository.findById(dto.getServiceTypeId()).orElseThrow());
        return converter.toDto(serviceRepository.save(entity));
    }

    @Override
    public ServiceDto update(Long id, ServiceDto patchData) {
        return serviceRepository
                .findById(id)
                .map(existing -> {
                    ServiceDto currentDto = converter.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    Service updatedEntity = converter.toEntity(currentDto);
                    updatedEntity.setServiceType(serviceTypeRepository
                            .findById(currentDto.getServiceTypeId())
                            .orElse(null));

                    return converter.toDto(serviceRepository.save(updatedEntity));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }
}
