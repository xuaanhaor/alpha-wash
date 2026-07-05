package com.alphawash.repository;

import com.alphawash.entity.OrderProductDtl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderProductDtlRepository extends JpaRepository<OrderProductDtl, Long> {
    List<OrderProductDtl> findByOrderDetail_CodeAndDeleteFlagFalse(String orderDetailCode);

    List<OrderProductDtl> findByOrderDetail_Code(String orderDetailCode);

    @Query(value = "SELECT COUNT(*) FROM order_product_dtl WHERE code LIKE :prefix%", nativeQuery = true)
    long countByCodeStartingWith(@Param("prefix") String prefix);
}
