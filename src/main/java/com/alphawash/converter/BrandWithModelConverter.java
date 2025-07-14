package com.alphawash.converter;

import com.alphawash.dto.BrandWithModelDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrandWithModelConverter {

    public static BrandWithModelDto map(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return null; // Hoặc ném ngoại lệ nếu cần
        }

        // Tất cả các hàng đều có cùng brandId và brandName
        Object[] firstRow = rows.get(0);
        Long brandId = ((Number) firstRow[3]).longValue();
        String brandName = (String) firstRow[4];

        List<BrandWithModelDto.Model> models = new ArrayList<>();

        for (Object[] row : rows) {
            BrandWithModelDto.Model model = BrandWithModelDto.Model.builder()
                    .id(((Number) row[0]).longValue()) // model_id
                    .modelName((String) row[1])
                    .size((String) row[2])
                    .build();
            models.add(model);
        }

        return BrandWithModelDto.builder()
                .id(brandId)
                .brandName(brandName)
                .models(models)
                .build();
    }

    public static List<BrandWithModelDto> mapList(List<Object[]> rows) {
        Map<Long, BrandWithModelDto> brandMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long brandId = ((Number) row[3]).longValue();
            String brandName = (String) row[4];

            // Tạo model
            BrandWithModelDto.Model model = BrandWithModelDto.Model.builder()
                    .id(((Number) row[0]).longValue()) // model_id
                    .modelName((String) row[1])
                    .size((String) row[2])
                    .build();

            // Nếu brand chưa tồn tại trong map → tạo mới
            brandMap.computeIfAbsent(brandId, id -> BrandWithModelDto.builder()
                            .id(id)
                            .brandName(brandName)
                            .models(new ArrayList<>())
                            .build())
                    .getModels()
                    .add(model);
        }

        return new ArrayList<>(brandMap.values());
    }
}
