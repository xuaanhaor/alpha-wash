package com.alphawash.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImportPreviewDto {
    private Integer totalRows;
    private Integer validRows;
    private Integer invalidRows;
    private List<ProductImportRowDto> rows;
}
