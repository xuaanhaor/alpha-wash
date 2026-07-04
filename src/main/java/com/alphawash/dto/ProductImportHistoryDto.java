package com.alphawash.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImportHistoryDto {
    private Long id;
    private String fileName;
    private String importedBy;
    private LocalDateTime importedAt;
    private Integer totalRows;
    private Integer successRows;
    private Integer failedRows;
    private Integer updatedRows;
    private Integer skippedRows;
    private String status;
    private String importMode;
    private String errorFilePath;
}
