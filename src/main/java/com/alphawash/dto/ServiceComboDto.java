package com.alphawash.dto;

import java.util.List;
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
public class ServiceComboDto {
    private Long id;
    private String code;
    private String comboName;
    private String note;
    private String price;
    List<String> serviceCatalogCodes;
}
