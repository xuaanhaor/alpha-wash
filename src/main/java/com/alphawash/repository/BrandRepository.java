package com.alphawash.repository;

import com.alphawash.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(value = "SELECT * FROM get_brand_with_model()", nativeQuery = true)
    List<Object[]> getBrandWithModel();
}
