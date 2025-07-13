package com.alphawash.service.impl;

import com.alphawash.converter.ServiceCatalogConverter;
import com.alphawash.dto.ServiceCatalogDto;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.repository.ServiceCatalogRepository;
import com.alphawash.repository.ServiceRepository;
import com.alphawash.service.ServiceCatalogService;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceCatalogRepository repository;
    private final ServiceRepository serviceRepository;
    private final ServiceCatalogConverter converter = ServiceCatalogConverter.INSTANCE;

    @Override
    public List<ServiceCatalogDto> getAll() {
        return converter.toDto(repository.findAll());
    }

    @Override
    public ServiceCatalogDto getById(Long id) {
        return repository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public ServiceCatalogDto create(ServiceCatalogDto dto) {
        ServiceCatalog entity = converter.toEntity(dto);
        entity.setService(serviceRepository.findById(dto.getServiceId()).orElse(null));
        return converter.toDto(repository.save(entity));
    }

    @Override
    public ServiceCatalogDto update(Long id, ServiceCatalogDto patchData) {
        return repository
                .findById(id)
                .map(existing -> {
                    ServiceCatalogDto currentDto = converter.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    ServiceCatalog patchedEntity = converter.toEntity(currentDto);
                    if (ObjectUtils.isNotNull(currentDto.getServiceId())) {
                        patchedEntity.setService(serviceRepository
                                .findById(currentDto.getServiceId())
                                .orElse(null));
                    }
                    return converter.toDto(repository.save(patchedEntity));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
