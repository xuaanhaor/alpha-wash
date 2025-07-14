package com.alphawash.repository;

import com.alphawash.entity.Brand;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(value = "SELECT * FROM get_brand_with_model()", nativeQuery = true)
    List<Object[]> getBrandWithModel();

    @Query(value = "SELECT * FROM get_models_by_brand_id(:brandId)", nativeQuery = true)
    List<Object[]> findModelsByBrandId(@Param("brandId") Long brandId);
}
