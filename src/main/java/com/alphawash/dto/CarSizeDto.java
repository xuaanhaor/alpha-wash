package com.alphawash.dto;

import com.alphawash.constant.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSizeDto {
    private String brandCode;
    private String modelCode;
    private String brandName;
    private String modelName;
    private Size size;
    private String note;
}
