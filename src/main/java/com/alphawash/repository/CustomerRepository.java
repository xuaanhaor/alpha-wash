package com.alphawash.repository;

import com.alphawash.dto.CustomerVehicleFlatDto;
import com.alphawash.entity.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findById(UUID id);

    @Query(value = "SELECT * FROM get_customer_vehicle_by_phone(:p_customer_phone)", nativeQuery = true)
    List<CustomerVehicleFlatDto> findCustomerWithVehicleByPhone(@Param("p_customer_phone") String phone);

    @Query(value = "SELECT * FROM get_customer_vehicle_by_license_plate(:p_customer_license_plate)", nativeQuery = true)
    List<CustomerVehicleFlatDto> findCustomerWithVehicleByLicensePlate(
            @Param("p_customer_license_plate") String licensePlate);

    Optional<Object> findByIdAndDeleteFlagFalse(UUID id);

    Optional<Customer> findByCustomerNameAndDeleteFlagFalse(String customerName);
}
