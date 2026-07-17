package com.alphawash.repository;

import com.alphawash.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findById(UUID id);

    List<Vehicle> findByCustomerId(UUID customerId);

    List<Vehicle> findByCustomerIdAndDeleteFlagFalse(UUID customerId);

    List<Vehicle> findByCustomerIdInAndDeleteFlagFalse(List<UUID> customerIds);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByNormalizedLicensePlateAndDeleteFlagFalse(String normalizedLicensePlate);

    List<Vehicle> findAllByNormalizedLicensePlateAndDeleteFlagFalse(String normalizedLicensePlate);

    @Query(
            value =
                    """
            select normalized_license_plate
            from vehicle
            where delete_flag = false and normalized_license_plate is not null
            group by normalized_license_plate
            having count(*) > 1""",
            nativeQuery = true)
    List<String> findDuplicateNormalizedPlates();

    @Query(
            value =
                    """
            select
                b.code as brand_code,
                m.code as model_code,
                b.brand_name,
                m.model_name,
                m.size,
                m.note
            from model m
            join brands b on m.brand_code = b.code""",
            nativeQuery = true)
    List<Object[]> findCar();

    @Query(value = "select * from get_customer_vehicle_services_used()", nativeQuery = true)
    List<Object[]> getVehicleServiceUsed();

    @Query(value = "select * from get_customer_vehicle_services_used()", nativeQuery = true)
    List<Object[]> searchVehicleServiceUsage();

    @Query(value = "select * from get_customer_vehicle_services_used_detail(:p_license_plate)", nativeQuery = true)
    List<Object[]> searchVehicleServiceUsageDetail(@Param("p_license_plate") String licensePlate);
}
