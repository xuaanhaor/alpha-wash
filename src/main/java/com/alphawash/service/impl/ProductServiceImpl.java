package com.alphawash.service.impl;

import com.alphawash.dto.ProductDto;
import com.alphawash.dto.ProductImageDto;
import com.alphawash.entity.Product;
import com.alphawash.entity.ProductCategory;
import com.alphawash.entity.ProductImage;
import com.alphawash.entity.Supplier;
import com.alphawash.repository.ProductCategoryRepository;
import com.alphawash.repository.ProductImageRepository;
import com.alphawash.repository.ProductRepository;
import com.alphawash.repository.SupplierRepository;
import com.alphawash.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductImageRepository imageRepository;

    @Override
    public List<ProductDto> getAll() {
        return repository.findByDeleteFlagFalseOrderByProductNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getActive() {
        return repository.findByIsActiveTrueAndDeleteFlagFalseOrderByProductNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ProductDto getByCode(String code) {
        return repository.findByCode(code).map(this::toDto).orElse(null);
    }

    @Override
    public ProductDto getByBarcode(String barcode) {
        return repository.findByBarcode(barcode).map(this::toDto).orElse(null);
    }

    @Override
    public List<ProductDto> getByCategoryCode(String categoryCode) {
        return repository.findByCategoryCode(categoryCode).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        Product entity = new Product();
        entity.setCode(generateCode());
        mapDtoToEntity(dto, entity);
        Product saved = repository.save(entity);

        if (dto.getImages() != null) {
            for (ProductImageDto imgDto : dto.getImages()) {
                ProductImage img = new ProductImage();
                img.setProduct(saved);
                img.setImageUrl(imgDto.getImageUrl());
                img.setDisplayOrder(imgDto.getDisplayOrder() != null ? imgDto.getDisplayOrder() : 0);
                img.setIsPrimary(imgDto.getIsPrimary() != null ? imgDto.getIsPrimary() : false);
                imageRepository.save(img);
            }
        }

        return toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto update(String code, ProductDto dto) {
        return repository.findByCode(code).map(entity -> {
            mapDtoToEntity(dto, entity);
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

    private void mapDtoToEntity(ProductDto dto, Product entity) {
        if (dto.getBarcode() != null) entity.setBarcode(dto.getBarcode());
        if (dto.getProductName() != null) entity.setProductName(dto.getProductName());
        if (dto.getCategoryCode() != null) {
            ProductCategory cat = categoryRepository.findByCode(dto.getCategoryCode()).orElse(null);
            entity.setCategory(cat);
        }
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getCostPrice() != null) entity.setCostPrice(dto.getCostPrice());
        if (dto.getSellingPrice() != null) entity.setSellingPrice(dto.getSellingPrice());
        if (dto.getMinPrice() != null) entity.setMinPrice(dto.getMinPrice());
        if (dto.getSuggestedPrice() != null) entity.setSuggestedPrice(dto.getSuggestedPrice());
        if (dto.getCurrentStock() != null) entity.setCurrentStock(dto.getCurrentStock());
        if (dto.getMinStock() != null) entity.setMinStock(dto.getMinStock());
        if (dto.getUnit() != null) entity.setUnit(dto.getUnit());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getTrackInventory() != null) entity.setTrackInventory(dto.getTrackInventory());
        if (dto.getSupplierCode() != null) {
            Supplier sup = supplierRepository.findByCode(dto.getSupplierCode()).orElse(null);
            entity.setDefaultSupplier(sup);
        }
        if (dto.getSupplierSku() != null) entity.setSupplierSku(dto.getSupplierSku());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }

    private String generateCode() {
        long count = repository.countByCodeStartingWith("PRD");
        return String.format("PRD%05d", count + 1);
    }

    private ProductDto toDto(Product entity) {
        List<ProductImageDto> images = imageRepository
                .findByProduct_CodeAndDeleteFlagFalseOrderByDisplayOrderAsc(entity.getCode())
                .stream()
                .map(img -> ProductImageDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .toList();

        return ProductDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .barcode(entity.getBarcode())
                .productName(entity.getProductName())
                .categoryCode(entity.getCategory() != null ? entity.getCategory().getCode() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getCategoryName() : null)
                .categoryColor(entity.getCategory() != null ? entity.getCategory().getColor() : null)
                .brand(entity.getBrand())
                .description(entity.getDescription())
                .costPrice(entity.getCostPrice())
                .sellingPrice(entity.getSellingPrice())
                .minPrice(entity.getMinPrice())
                .suggestedPrice(entity.getSuggestedPrice())
                .currentStock(entity.getCurrentStock())
                .minStock(entity.getMinStock())
                .unit(entity.getUnit())
                .location(entity.getLocation())
                .trackInventory(entity.getTrackInventory())
                .supplierCode(entity.getDefaultSupplier() != null ? entity.getDefaultSupplier().getCode() : null)
                .supplierName(entity.getDefaultSupplier() != null ? entity.getDefaultSupplier().getSupplierName() : null)
                .supplierSku(entity.getSupplierSku())
                .isActive(entity.getIsActive())
                .images(images)
                .exclusiveKey(entity.getExclusiveKey())
                .build();
    }
}
