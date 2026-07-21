package com.alphawash.repository;

import com.alphawash.constant.ServiceCategory;
import com.alphawash.entity.ServiceItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, UUID> {

    List<ServiceItem> findByCategoryAndActiveTrueAndDeleteFlagFalseOrderBySortOrderAsc(ServiceCategory category);

    List<ServiceItem> findByActiveTrueAndDeleteFlagFalseOrderByCategoryAscSortOrderAsc();

    List<ServiceItem> findByCategoryAndDeleteFlagFalseOrderBySortOrderAsc(ServiceCategory category);

    List<ServiceItem> findByDeleteFlagFalseOrderByCategoryAscSortOrderAsc();

    List<ServiceItem> findByCanBeBonusTrueAndActiveTrueAndDeleteFlagFalse();

    long countByDeleteFlagFalse();
}
