package com.alphawash.service.impl;

import com.alphawash.dto.ProductCategoryDto;
import com.alphawash.entity.ProductCategory;
import com.alphawash.repository.ProductCategoryRepository;
import com.alphawash.service.ProductCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository repository;

    @Override
    public List<ProductCategoryDto> getAll() {
        return repository.findByDeleteFlagFalseOrderByDisplayOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProductCategoryDto> getActive() {
        return repository.findByIsActiveTrueAndDeleteFlagFalseOrderByDisplayOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ProductCategoryDto getByCode(String code) {
        return repository.findByCode(code).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public ProductCategoryDto create(ProductCategoryDto dto) {
        ProductCategory entity = new ProductCategory();
        entity.setCode(generateCode());
        entity.setCategoryName(dto.getCategoryName());
        entity.setColor(dto.getColor());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public ProductCategoryDto update(String code, ProductCategoryDto dto) {
        return repository.findByCode(code).map(entity -> {
            if (dto.getCategoryName() != null) entity.setCategoryName(dto.getCategoryName());
            if (dto.getColor() != null) entity.setColor(dto.getColor());
            if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
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
        long count = repository.countByCodeStartingWith("PCAT");
        return String.format("PCAT%02d", count + 1);
    }

    private ProductCategoryDto toDto(ProductCategory entity) {
        return ProductCategoryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .categoryName(entity.getCategoryName())
                .color(entity.getColor())
                .displayOrder(entity.getDisplayOrder())
                .isActive(entity.getIsActive())
                .exclusiveKey(entity.getExclusiveKey())
                .build();
    }
}
