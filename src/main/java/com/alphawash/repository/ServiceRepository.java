package com.alphawash.repository;

import com.alphawash.entity.Service;
import com.alphawash.response.BasicServiceResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByCode(String code);

    @Query(value = "SELECT * FROM get_basic_services()", nativeQuery = true)
    List<BasicServiceResponse> getBasicServices();

    @Query(
            value =
                    """
    SELECT sc.id                AS service_id,
           st.code              AS service_type_code,
           st.service_type_name AS service_type_name,
           s.code               AS service_code,
           s.service_name       AS service_name,
           sc.code              AS service_catalog_code,
           sc.price,
           s.duration,
           sc.size,
           s.note::text
    FROM service s
             JOIN service_type st ON s.service_type_code = st.code
             JOIN service_catalog sc ON s.code = sc.service_code
    WHERE s.code = :p_service_code
      AND sc.size = :p_size
      AND s.delete_flag = false
      AND st.delete_flag = false
      AND sc.delete_flag = false
    ORDER BY st.code, s.code
    """,
            nativeQuery = true)
    Optional<BasicServiceResponse> getBasicServiceByServiceCode(
            @Param("p_service_code") String serviceCode, @Param("p_size") String size);

    @Query(value = "SELECT * FROM service s WHERE s.delete_flag = false", nativeQuery = true)
    List<Service> getServicesAvailable();
}
