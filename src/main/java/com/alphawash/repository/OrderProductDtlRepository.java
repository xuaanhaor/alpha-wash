package com.alphawash.repository;

import com.alphawash.entity.OrderProductDtl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductDtlRepository extends JpaRepository<OrderProductDtl, Long> {
    List<OrderProductDtl> findByOrderDetail_CodeAndDeleteFlagFalse(String orderDetailCode);
}
