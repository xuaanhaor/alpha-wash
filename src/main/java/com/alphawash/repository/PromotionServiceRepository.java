package com.alphawash.repository;

import com.alphawash.entity.PromotionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PromotionServiceRepository extends JpaRepository<PromotionService, UUID> {

    @Query("""
                SELECT
                    ps.promotionId,
                    ps.serviceCode,
                    s.serviceName,
                    ps.discountAmount,
                    ps.discountPercent
                FROM PromotionService ps
                JOIN Service s ON s.code = ps.serviceCode
                WHERE ps.deleteFlag = false
                  AND s.deleteFlag = false
                  AND ps.promotionId IN :promotionIds
            """)
    List<Object[]> findServiceItemsWithNameByPromotionIds(@Param("promotionIds") List<UUID> promotionIds);
}