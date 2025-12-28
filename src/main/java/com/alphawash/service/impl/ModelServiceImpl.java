package com.alphawash.service.impl;

import com.alphawash.constant.Size;
import com.alphawash.converter.BrandConverter;
import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.ModelDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Model;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.service.BrandService;
import com.alphawash.service.ModelService;

import java.util.List;

import com.alphawash.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;

    private final BrandService brandService;
    private final BrandConverter converterBrand = BrandConverter.INSTANCE;

    @Override
    @Transactional
    public ModelDto create(ModelDto dto) {

        // 1. Kiểm tra validate
        if (StringUtils.isNullOrBlank(dto.getModelName())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Model name không được để trống");
        }
        if (StringUtils.isNullOrBlank(dto.getSize())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Size không được để trống");
        }

        // 2. Xử lý hãng xe
        Brand brand;
        if (!StringUtils.isNullOrBlank(dto.getBrandCode())) {
            // Trường hợp 1: Brand đã tồn tại, lấy brand ra
            brand = brandRepository.findByCode(dto.getBrandCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy brand với code: " + dto.getBrandCode()));
        } else {
            // Trường hợp 2: Tạo brand mới
            if (StringUtils.isNullOrBlank(dto.getBrandName())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Brand name không được để trống khi tạo brand mới");
            }

            BrandDto newBrand = brandService.upsert(BrandDto.builder().brandName(dto.getBrandName()).build());
            brand = converterBrand.toEntity(newBrand);
        }

        // 3. Tạo Model
        Model model = Model.builder().code(generateModelCode()).modelName(dto.getModelName()).size(Size.fromString(dto.getSize())).note(dto.getNote()).brand(brand).build();

        // 4. Tạo mới
        Model saved = modelRepository.save(model);

        return ModelDto.builder().modelId(saved.getId()).modelCode(saved.getCode()).modelName(saved.getModelName()).size(String.valueOf(saved.getSize())).brandCode(saved.getBrand().getCode()).brandName(saved.getBrand().getBrandName()).note(saved.getNote()).build();
    }

    @Transactional
    @Override
    public ModelDto update(ModelDto dto) {

        if (StringUtils.isNullOrBlank(dto.getModelCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "modelCode không được để trống");
        }

        // 1 Lấy model hiện tại
        Model model = modelRepository.findByCode(dto.getModelCode().trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy model với code: " + dto.getModelCode()));

        // 2 Update các field cho phép
        if (!StringUtils.isNullOrBlank(dto.getModelName())) {
            model.setModelName(dto.getModelName().trim());
        }

        if (!StringUtils.isNullOrBlank(dto.getSize())) {
            model.setSize(Size.fromString(dto.getSize().trim()));
        }

        // note cho phép null để clear
        model.setNote(dto.getNote());

        // 3 Nếu có brandCode mới thì đổi brand
        if (!StringUtils.isNullOrBlank(dto.getBrandCode())) {
            Brand brand = brandRepository.findByCode(dto.getBrandCode().trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy brand với code: " + dto.getBrandCode()));
            model.setBrand(brand);
        }

        // 4 Cập nhật
        Model saved = modelRepository.save(model);

        // 5 Return DTO
        return ModelDto.builder().modelId(saved.getId()).modelCode(saved.getCode()).modelName(saved.getModelName()).size(String.valueOf(saved.getSize())).brandCode(saved.getBrand() != null ? saved.getBrand().getCode() : null).brandName(saved.getBrand() != null ? saved.getBrand().getBrandName() : null).note(saved.getNote()).build();
    }

    @Transactional
    @Override
    public boolean delete(String code) {
        // 1. Kiểm tra có tồn tại không
        Model model = modelRepository.findByCode(code).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Model không tồn tại hoặc đã bị xóa"));
        // 2. Cập nhật deleteFlag = true
        model.setDeleteFlag(true);
        // 3. Cập nhật brand
        Model saved = modelRepository.save(model);
        // 4. Kiểm tra
        return Boolean.TRUE.equals(saved.getDeleteFlag());
    }

    @Override
    public List<ModelWithoutBrandDto> findByBrandCode(String brandCode) {
        var list = modelRepository.findByBrandCode(brandCode);
        return list.stream().map(model -> new ModelWithoutBrandDto(model.getId(), model.getCode(), model.getModelName(), model.getSize().getValue())).toList();
    }

    private String generateModelCode() {
        String maxCode = modelRepository.findMaxModelCode();
        int nextNumber = 1;
        if (maxCode != null && !maxCode.isBlank()) {
            nextNumber = Integer.parseInt(maxCode.substring(1)) + 1;
        }
        return "M" + String.format("%04d", nextNumber);
    }
}
