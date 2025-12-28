package com.alphawash.repository;

import com.alphawash.entity.CustomerComboQuota;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerComboQuotaRepository extends JpaRepository<CustomerComboQuota, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT q FROM CustomerComboQuota q
        WHERE q.customerComboSummary.code = :comboCode
          AND q.serviceCatalog.code = :serviceCatalogCode
          AND q.deleteFlag = false
    """)
    Optional<CustomerComboQuota> lockByComboAndService(
            @Param("comboCode") String comboCode,
            @Param("serviceCatalogCode") String serviceCatalogCode
    );

    @Modifying
    @Query(value = """
                UPDATE customer_combo_summary
                SET status = :status,
                    updated_at = CURRENT_TIMESTAMP
                WHERE code = :code
                  AND delete_flag = FALSE
            """, nativeQuery = true)
    void setStatus(String code, String status);

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM customer_combo_quota q
                    WHERE q.customer_combo_summary_code = :comboCardCode
                      AND q.delete_flag = FALSE
                      AND (q.total_uses - q.used_uses) > 0
                )
            """, nativeQuery = true)
    boolean existsRemaining(String comboCardCode);
}