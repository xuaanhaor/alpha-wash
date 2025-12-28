package com.alphawash.repository;

import com.alphawash.entity.ServiceCombo;

import java.util.List;

import com.alphawash.response.ComboGetAllResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceComboRepository extends JpaRepository<ServiceCombo, Integer> {

    @Query(value = """
            SELECT
                sc.code,
                sc.combo_name,
                sc.duration_days,
                sc.status,
            
                scc.code,
                scc.combo_size,
                scc.price,
                scc.price_include_tax,
            
                scq.service_catalog_code,
                scq.quality
            FROM service_combo sc
            JOIN service_combo_catalog scc
                ON scc.combo_code = sc.code
            JOIN service_combo_quality scq
                ON scq.combo_catalog_code = scc.code
            WHERE sc.delete_flag = FALSE
              AND scc.delete_flag = FALSE
              AND scq.delete_flag = FALSE
            ORDER BY sc.code, scc.combo_size, scq.service_catalog_code
            """, nativeQuery = true)
    List<Object[]> findAllComboFlatRaw();

    @Query(value = """
            SELECT *
            FROM service_combo
            WHERE code = :code
              AND delete_flag = FALSE
            """, nativeQuery = true)
    ServiceCombo findByCode(String code);

    @Query(value = """
                SELECT COALESCE(SUM(scq.quality), 0)
                FROM service_combo_quality scq
                WHERE scq.combo_catalog_code = :comboCatalogCode
                  AND scq.delete_flag = FALSE
            """, nativeQuery = true)
    Integer sumQuantityByComboCatalogCode(@Param("comboCatalogCode") String comboCatalogCode);
}