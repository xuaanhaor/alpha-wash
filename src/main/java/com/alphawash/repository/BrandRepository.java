package com.alphawash.repository;

import com.alphawash.entity.Brand;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(
            value = "SELECT code " +
                    "FROM brands " +
                    "WHERE delete_flag = false AND code LIKE 'B%' " +
                    "ORDER BY code DESC " +
                    "LIMIT 1",
            nativeQuery = true
    )
    String findMaxBrandCode();

    @Query(
            value = "SELECT * " +
                    "FROM brands " +
                    "WHERE code = :code AND delete_flag = false " +
                    "LIMIT 1",
            nativeQuery = true
    )
    Optional<Brand> findByCode(@Param("code") String code);

    @Query(
            value = "SELECT * FROM brands WHERE delete_flag = false ORDER BY code ASC",
            nativeQuery = true
    )
    Optional<List<Brand>> findAllActive();

    @Query(
            value = """
                        SELECT
                            b.code        AS brand_code,
                            b.brand_name AS brand_name,
                            m.code        AS model_code,
                            m.model_name AS model_name,
                            m.size        AS size
                        FROM brands b
                        LEFT JOIN model m
                            ON m.brand_code = b.code
                           AND m.delete_flag = false
                        WHERE b.delete_flag = false
                        ORDER BY b.code, m.code
                    """,
            nativeQuery = true
    )
    List<Object[]> getAllBrandWithModels();

    @Query(
            value = """
                        SELECT
                            b.code        AS brand_code,
                            b.brand_name AS brand_name,
                            m.code        AS model_code,
                            m.model_name AS model_name,
                            m.size        AS size
                        FROM brands b
                        LEFT JOIN model m
                            ON m.brand_code = b.code
                           AND m.delete_flag = false
                        WHERE b.delete_flag = false
                          AND b.code = :brandCode
                        ORDER BY m.code
                    """,
            nativeQuery = true
    )
    List<Object[]> getBrandWithModelsByCode(@Param("brandCode") String brandCode);
}
