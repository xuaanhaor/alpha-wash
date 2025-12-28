package com.alphawash.service.impl;

import com.alphawash.converter.BrandConverter;
import com.alphawash.converter.BrandWithModelConverter;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import com.alphawash.entity.Brand;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.service.BrandService;
import com.alphawash.util.PatchHelper;
import java.util.List;

import com.alphawash.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandConverter converter = BrandConverter.INSTANCE;

    @Override
    public List<BrandDto> getAll() {
        //1. Lấy danh sách hãng xe
        List<Brand> brandList = brandRepository.findAllActive()
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy Hãng Xe nào."));
        //2. Trả kết quả
        return brandList.stream()
                .map(b -> BrandDto.builder()
                        .brandId(b.getId())
                        .brandCode(b.getCode())
                        .brandName(b.getBrandName())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public BrandDto upsert(BrandDto dto) {
        // 1. Check null Brand Name
        if (StringUtils.isNullOrBlank(dto.getBrandName())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Brand name không được để trống");
        }
        //2. Kiểm tra Brand codo có trong không
        if (StringUtils.isNullOrBlank(dto.getBrandCode())) {
            // 2.1. Null => tạo mới brand
            // 2.1.1. Tạo Brand Code
            String newCode = generateBrandCode();
            // 2.1.2. Map DTO -> Entity
            Brand entity = converter.toEntity(dto);
            entity.setCode(newCode);
            // 2.1.3. Tạo brand mới
            Brand saved = brandRepository.save(entity);
            // 2.1.4. Return DTO
            return converter.toDto(saved);
        } else {
            // 2.2. Not null => Cập nhật
            // 2.2.1. Kiểm tra có tồn tại không
            Brand brand = brandRepository.findByCode(dto.getBrandCode())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy hãng xe với brandCode: " + dto.getBrandCode()));
            // 2.2.2. Thay đổi brand name
            brand.setBrandName(dto.getBrandName().trim());
            // 2.2.3. Cập nhật brand mới
            Brand saved = brandRepository.save(brand);
            // 2.2.4. Return DTO
            return converter.toDto(saved);
        }
    }

    @Override
    @Transactional
    public boolean delete(String code) {
        // 1. Kiểm tra có tồn tại không
        Brand brand = brandRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy hãng xe với brandCode: " + code));
        // 2. Cập nhật deleteFlag = true
        brand.setDeleteFlag(true);
        // 3. Cập nhật brand
        Brand saved = brandRepository.save(brand);
        // 4. Kiểm tra
        return Boolean.TRUE.equals(saved.getDeleteFlag());
    }

    @Override
    public List<BrandWithModelDto> getBrandWithModel() {
        List<Object[]> rows = brandRepository.getAllBrandWithModels();
        return BrandWithModelConverter.mapList(rows);
    }

    @Override
    public BrandWithModelDto getBrandWithModelByBrandCode(String code) {
        List<Object[]> row = brandRepository.getBrandWithModelsByCode(code);
        return BrandWithModelConverter.map(row);
    }

    private String generateBrandCode() {
        String maxCode = brandRepository.findMaxBrandCode();
        int nextNumber = 1;
        if (maxCode != null && !maxCode.startsWith("B")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,"Brand code không hợp lệ trong DB");
        }
        if (maxCode != null) {
            nextNumber = Integer.parseInt(maxCode.substring(1)) + 1;
        }
        return "B" + String.format("%04d", nextNumber);
    }

}
