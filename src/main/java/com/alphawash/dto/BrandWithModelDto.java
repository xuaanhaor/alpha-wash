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
    private Long id;
    private String brandName;
    private List<Model> models;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Model {
        private Long id;
        private String modelName;
        private String size;
    }
}
