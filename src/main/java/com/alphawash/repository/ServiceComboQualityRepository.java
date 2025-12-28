package com.alphawash.repository;

import com.alphawash.entity.ServiceComboQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceComboQualityRepository extends JpaRepository<ServiceComboQuality, Integer> {

    @Query(value = """
                SELECT scq.*
                FROM service_combo_quality scq
                WHERE scq.combo_catalog_code = :comboCatalogCode
                  AND scq.delete_flag = FALSE
            """, nativeQuery = true)
    List<ServiceComboQuality> findByComboCatalogCode(@Param("comboCatalogCode") String comboCatalogCode);


}
