package com.alphawash.repository;

import com.alphawash.entity.ServiceCategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, UUID> {

    Optional<ServiceCategoryEntity> findByCodeAndDeleteFlagFalse(String code);

    boolean existsByCodeAndDeleteFlagFalse(String code);

    boolean existsByCodeAndDeleteFlagFalseAndIdNot(String code, UUID id);

    List<ServiceCategoryEntity> findAllByDeleteFlagFalseOrderBySortOrderAsc();

    List<ServiceCategoryEntity> findAllByActiveTrueAndDeleteFlagFalseOrderBySortOrderAsc();

    long countByDeleteFlagFalse();
}
