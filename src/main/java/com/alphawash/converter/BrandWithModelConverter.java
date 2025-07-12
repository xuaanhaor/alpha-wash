package com.alphawash.converter;

import com.alphawash.dto.BrandWithModelDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrandWithModelConverter {
    public static List<BrandWithModelDto> mapList(List<Object[]> rows) {
        Map<Long, BrandWithModelDto> brandMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long brandId = ((Number) row[3]).longValue();
            String brandName = (String) row[4];

            // Tạo model
            BrandWithModelDto.Model model = BrandWithModelDto.Model.builder()
                    .id(((Number) row[0]).longValue())  // model_id
                    .modelName((String) row[1])
                    .size((String) row[2])
                    .build();

            // Nếu brand chưa tồn tại trong map → tạo mới
            brandMap.computeIfAbsent(brandId, id -> BrandWithModelDto.builder()
                    .id(id)
                    .brandName(brandName)
                    .models(new ArrayList<>())
                    .build()
            ).getModels().add(model);
        }

        return new ArrayList<>(brandMap.values());
    }
}
