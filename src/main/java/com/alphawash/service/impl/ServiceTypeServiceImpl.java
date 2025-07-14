package com.alphawash.service.impl;

import com.alphawash.converter.ServiceTypeConverter;
import com.alphawash.dto.ServiceTypeDto;
import com.alphawash.entity.ServiceType;
import com.alphawash.repository.ServiceTypeRepository;
import com.alphawash.service.ServiceTypeService;
import com.alphawash.util.PatchHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final ServiceTypeConverter converter = ServiceTypeConverter.INSTANCE;

    @Override
    public List<ServiceTypeDto> getAll() {
        return converter.toDto(repository.findAll());
    }

    @Override
    public ServiceTypeDto getById(Long id) {
        return repository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public ServiceTypeDto create(ServiceTypeDto dto) {
        return converter.toDto(repository.save(converter.toEntity(dto)));
    }

    @Override
    public ServiceTypeDto update(Long id, ServiceTypeDto patchData) {
        return repository.findById(id).map(existing -> {
            ServiceTypeDto currentDto = converter.toDto(existing);
            PatchHelper.applyPatch(patchData, currentDto);
            ServiceType updatedEntity = converter.toEntity(currentDto);
            return converter.toDto(repository.save(updatedEntity));
        }).orElse(null);
    }


    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
