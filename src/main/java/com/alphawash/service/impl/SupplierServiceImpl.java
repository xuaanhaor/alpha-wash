package com.alphawash.service.impl;

import com.alphawash.dto.SupplierDto;
import com.alphawash.entity.Supplier;
import com.alphawash.repository.SupplierRepository;
import com.alphawash.service.SupplierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository repository;

    @Override
    public List<SupplierDto> getAll() {
        return repository.findByDeleteFlagFalseOrderBySupplierNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<SupplierDto> getActive() {
        return repository.findByIsActiveTrueAndDeleteFlagFalseOrderBySupplierNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public SupplierDto getByCode(String code) {
        return repository.findByCode(code).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public SupplierDto create(SupplierDto dto) {
        Supplier entity = new Supplier();
        entity.setCode(generateCode());
        entity.setSupplierName(dto.getSupplierName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setTaxId(dto.getTaxId());
        entity.setNotes(dto.getNotes());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SupplierDto update(String code, SupplierDto dto) {
        return repository.findByCode(code).map(entity -> {
            if (dto.getSupplierName() != null) entity.setSupplierName(dto.getSupplierName());
            if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
            if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
            if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
            if (dto.getTaxId() != null) entity.setTaxId(dto.getTaxId());
            if (dto.getNotes() != null) entity.setNotes(dto.getNotes());
            if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
            return toDto(repository.save(entity));
        }).orElse(null);
    }

    @Override
    @Transactional
    public void delete(String code) {
        repository.findByCode(code).ifPresent(entity -> {
            entity.setDeleteFlag(true);
            repository.save(entity);
        });
    }

    private String generateCode() {
        long count = repository.countByCodeStartingWith("SUP");
        return String.format("SUP%03d", count + 1);
    }

    private SupplierDto toDto(Supplier entity) {
        return SupplierDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .supplierName(entity.getSupplierName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .taxId(entity.getTaxId())
                .notes(entity.getNotes())
                .isActive(entity.getIsActive())
                .exclusiveKey(entity.getExclusiveKey())
                .build();
    }
}
