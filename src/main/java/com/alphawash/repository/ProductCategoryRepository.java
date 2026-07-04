package com.alphawash.repository;

import com.alphawash.entity.ProductCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByCode(String code);

    List<ProductCategory> findByDeleteFlagFalseOrderByDisplayOrderAsc();

    List<ProductCategory> findByIsActiveTrueAndDeleteFlagFalseOrderByDisplayOrderAsc();

    long countByCodeStartingWith(String prefix);
}
