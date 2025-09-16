package com.alphawash.repository;

import com.alphawash.dto.ComboCatalogDto;
import com.alphawash.entity.ServiceComboDtl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceComboDtlRepository extends JpaRepository<ServiceComboDtl, Long> {
    @Query("SELECT new com.alphawash.dto.ComboCatalogDto(d.comboCode, d.serviceCatalogCode)"
            + " FROM ServiceComboDtl d WHERE d.deleteFlag = false")
    List<ComboCatalogDto> findServiceCatalogCodes();

    @Query("SELECT d.serviceCatalogCode FROM ServiceComboDtl d WHERE d.comboCode = ?1 AND d.deleteFlag = false")
    List<String> findServiceCatalogCodesByComboCode(String comboCode);

    @Modifying
    @Query("UPDATE ServiceComboDtl d SET d.deleteFlag = true WHERE d.comboCode in :comboCodes")
    int deleteByComboCode(@Param("comboCodes") String comboCodes);

    int deleteByComboCodeIn(List<String> comboCodes);

    int countByComboCodeInAndDeleteFlagTrue(List<String> comboCodes);
}
