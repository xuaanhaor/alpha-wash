package com.alphawash.repository;

import com.alphawash.entity.CustomerPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CustomerPromotionRepository extends JpaRepository<CustomerPromotion, UUID> {

    @Query(value = """
                SELECT COUNT(*)
                FROM customer_promotion cp
                WHERE cp.delete_flag = false
                  AND cp.customer_id = :customerId
                  AND cp.promotion_id = :promotionId
            """, nativeQuery = true)
    long countUsed(@Param("customerId") UUID customerId,
                   @Param("promotionId") UUID promotionId);
}