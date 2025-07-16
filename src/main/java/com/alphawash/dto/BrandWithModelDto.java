package com.alphawash.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandWithModelDto {
    private String code;
    private String brandName;
    private List<Model> models;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Model {
        private String code;
        private String modelName;
        private String size;
    }
}
