package com.alphawash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    public static class Model{
        private Long id;
        private String modelName;
        private String size;
    }
}
