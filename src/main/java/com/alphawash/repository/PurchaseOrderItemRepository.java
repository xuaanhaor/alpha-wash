package com.alphawash.repository;

import com.alphawash.entity.PurchaseOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByPurchaseOrder_CodeAndDeleteFlagFalse(String poCode);
}
