package com.alphawash.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImportResultDto {
    private Long importHistoryId;
    private Integer totalRows;
    private Integer importedRows;
    private Integer updatedRows;
    private Integer skippedRows;
    private Integer failedRows;
    private List<ProductImportRowDto> failedDetails;
}
