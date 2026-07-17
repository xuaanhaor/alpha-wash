package com.alphawash.repository;

import com.alphawash.entity.Supplier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByCode(String code);

    List<Supplier> findByDeleteFlagFalseOrderBySupplierNameAsc();

    List<Supplier> findByIsActiveTrueAndDeleteFlagFalseOrderBySupplierNameAsc();

    long countByCodeStartingWith(String prefix);
}
