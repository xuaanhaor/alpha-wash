package com.alphawash.repository;

import com.alphawash.entity.Model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModelRepository extends JpaRepository<Model, Long> {
    @Query(value = "SELECT * FROM model WHERE code = :code AND delete_flag = false LIMIT 1", nativeQuery = true)
    Optional<Model> findByCode(@Param("code") String code);

    @Query(value = """
                SELECT *
                FROM model
                WHERE brand_code = :brandCode
                  AND delete_flag = false
                ORDER BY code ASC
            """, nativeQuery = true)
    List<Model> findByBrandCode(@Param("brandCode") String brandCode);

    @Query(value = """
                SELECT code
                FROM model
                WHERE delete_flag = false AND code LIKE 'M%'
                ORDER BY code DESC
                LIMIT 1
            """, nativeQuery = true)
    String findMaxModelCode();
}
