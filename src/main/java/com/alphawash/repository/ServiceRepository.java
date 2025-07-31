package com.alphawash.repository;

import com.alphawash.entity.Service;
import com.alphawash.response.BasicServiceResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByCode(String code);

    @Query(value = "SELECT * FROM get_basic_services()", nativeQuery = true)
    List<BasicServiceResponse> getBasicServices();
}
