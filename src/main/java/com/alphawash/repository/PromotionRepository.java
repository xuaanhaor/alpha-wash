package com.alphawash.repository;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionStatus;
import com.alphawash.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Boolean existsByPromoCode(@Param("code") String promoCode);

    @Query(value = """
                SELECT * FROM promotion p
                WHERE p.delete_flag = false
                  AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text))
                  AND (CAST(:promoType AS text) IS NULL OR p.promo_type = CAST(:promoType AS text))
                  AND (CAST(:keyword AS text) IS NULL OR CAST(:keyword AS text) = '' OR
                       LOWER(p.promo_code) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR
                       LOWER(p.promo_name) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
                  AND (CAST(:fromDate AS timestamp) IS NULL OR p.end_date IS NULL OR p.end_date >= CAST(:fromDate AS timestamp))
                  AND (CAST(:toDate   AS timestamp) IS NULL OR p.start_date <= CAST(:toDate   AS timestamp))
                ORDER BY p.created_at DESC
            """,
            countQuery = """
                        SELECT COUNT(*) FROM promotion p
                        WHERE p.delete_flag = false
                          AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text))
                          AND (CAST(:promoType AS text) IS NULL OR p.promo_type = CAST(:promoType AS text))
                          AND (CAST(:keyword AS text) IS NULL OR CAST(:keyword AS text) = '' OR
                               LOWER(p.promo_code) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR
                               LOWER(p.promo_name) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
                          AND (CAST(:fromDate AS timestamp) IS NULL OR p.end_date IS NULL OR p.end_date >= CAST(:fromDate AS timestamp))
                          AND (CAST(:toDate   AS timestamp) IS NULL OR p.start_date <= CAST(:toDate   AS timestamp))
                    """,
            nativeQuery = true)
    Page<Promotion> findAllFilter(
            @Param("status") PromotionStatus status,
            @Param("promoType") PromoType promoType,
            @Param("keyword") String keyword,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query(value = """
                SELECT *
                FROM promotion p
                WHERE p.delete_flag = false
                  AND p.status = 'ACTIVE'
                  AND p.start_date <= NOW()
                  AND (p.end_date IS NULL OR p.end_date >= NOW())
                ORDER BY p.created_at DESC
            """, nativeQuery = true)
    List<Promotion> findActiveNow();

}
