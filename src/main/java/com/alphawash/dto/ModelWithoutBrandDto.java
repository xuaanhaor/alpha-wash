package com.alphawash.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelWithoutBrandDto {
    private Long id;
    private String code;
    private String modelName;
    private String size;
}
