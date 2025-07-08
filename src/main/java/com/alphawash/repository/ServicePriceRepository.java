package com.alphawash.repository;

import com.alphawash.entity.ServicePrice;
import com.alphawash.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, Integer> {

    List<ServicePrice> findByService(Service service);

    ServicePrice findByServiceAndSize(Service service, String size);
}
