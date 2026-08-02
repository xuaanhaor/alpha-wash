package com.alphawash.repository;

import com.alphawash.entity.Brand;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(value = "SELECT * FROM get_brand_with_model()", nativeQuery = true)
    List<Object[]> getBrandWithModel();

    @Query(value = "SELECT * FROM get_models_by_brand_code(:brandCode)", nativeQuery = true)
    List<Object[]> findModelsByBrandId(@Param("brandCode") String brandCode);

    Optional<Brand> findByCode(String code);

    @Query(value = """
        SELECT 'B' || LPAD((COALESCE(MAX(SUBSTRING(code, 2)::INT), 0) + 1)::TEXT, 4, '0')
        FROM brands
        """, nativeQuery = true)
    String generateBrandCode();
}
