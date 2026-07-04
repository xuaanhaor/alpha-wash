package com.alphawash.repository;

import com.alphawash.constant.InventoryTransactionType;
import com.alphawash.entity.InventoryTransaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProduct_CodeAndDeleteFlagFalseOrderByCreatedAtDesc(String productCode);

    List<InventoryTransaction> findByDeleteFlagFalseOrderByCreatedAtDesc();

    List<InventoryTransaction> findByTypeAndDeleteFlagFalseOrderByCreatedAtDesc(InventoryTransactionType type);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCodeStartingWith(String prefix);
}
