package com.alphawash.repository;

import com.alphawash.entity.Vehicle;
import com.alphawash.entity.Customer;
import com.alphawash.entity.VehicleCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByLicencePlate(String licencePlate);

    List<Vehicle> findByCustomer(Customer customer);

    List<Vehicle> findByCatalog(VehicleCatalog catalog);
}
