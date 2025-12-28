package com.alphawash.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.alphawash.entity.CustomerComboSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerComboSummaryRepository extends JpaRepository<CustomerComboSummary, UUID> {

    @Query("""
        SELECT c FROM CustomerComboSummary c
        WHERE c.code = :code
          AND c.status = 'ACTIVE'
          AND c.deleteFlag = false
    """)
    Optional<CustomerComboSummary> findActiveByCode(@Param("code") String code);

    @Modifying
    @Query("""
        UPDATE CustomerComboSummary c
        SET c.usedUses = COALESCE(c.usedUses, 0) + :inc
        WHERE c.code = :code
    """)
    void increaseUsedUses(@Param("code") String code, @Param("inc") int inc);

    @Modifying
    @Query(value = """
                UPDATE customer_combo_summary
                SET status = :status,
                    updated_at = CURRENT_TIMESTAMP
                WHERE code = :code
                  AND delete_flag = FALSE
            """, nativeQuery = true)
    void setStatus(String code, String status);
}