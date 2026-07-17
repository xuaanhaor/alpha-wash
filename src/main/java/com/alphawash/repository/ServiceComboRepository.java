package com.alphawash.repository;

import com.alphawash.entity.ServiceCombo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ServiceComboRepository extends JpaRepository<ServiceCombo, Long> {
    Optional<ServiceCombo> findByCode(String code);

    @Modifying
    @Query("UPDATE ServiceCombo d SET d.deleteFlag = true WHERE d.code IN :comboCodes")
    int deleteByCodes(List<String> comboCodes);

    int countByCodeInAndDeleteFlagTrue(List<String> comboCodes);
}
