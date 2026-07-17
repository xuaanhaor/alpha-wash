package com.alphawash.repository;

import com.alphawash.entity.ProductImportHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImportHistoryRepository extends JpaRepository<ProductImportHistory, Long> {
    List<ProductImportHistory> findByDeleteFlagFalseOrderByImportedAtDesc();
}
