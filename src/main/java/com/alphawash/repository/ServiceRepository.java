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

    @Query(value = "SELECT * FROM get_basic_service_by_code(:p_service_code)", nativeQuery = true)
    Optional<BasicServiceResponse> getBasicServiceByServiceCode(@Param("p_service_code") String serviceCode);
}
