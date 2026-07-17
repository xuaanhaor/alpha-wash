package com.alphawash.repository;

import com.alphawash.entity.PurchaseOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByCode(String code);

    List<PurchaseOrder> findByDeleteFlagFalseOrderByCreatedAtDesc();

    long countByCodeStartingWith(String prefix);
}
