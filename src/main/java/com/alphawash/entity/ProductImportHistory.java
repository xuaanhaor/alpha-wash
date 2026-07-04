package com.alphawash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "product_import_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImportHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "imported_by", nullable = false)
    private String importedBy;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "success_rows", nullable = false)
    private Integer successRows;

    @Column(name = "failed_rows", nullable = false)
    private Integer failedRows;

    @Column(name = "updated_rows", nullable = false)
    private Integer updatedRows;

    @Column(name = "skipped_rows", nullable = false)
    private Integer skippedRows;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "import_mode", nullable = false)
    private String importMode;

    @Column(name = "error_file_path", columnDefinition = "TEXT")
    private String errorFilePath;
}
