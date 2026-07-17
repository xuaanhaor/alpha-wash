package com.alphawash.repository;

import com.alphawash.entity.Vehicle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutoLinkRepository extends JpaRepository<Vehicle, UUID> {

    /**
     * Returns one row per (vehicle, customer) pair for all unlinked vehicles that have at least one order.
     * Columns: vehicle_id (UUID), license_plate (String), customer_id (UUID), customer_name (String), order_count (Long)
     * Ordered by vehicle_id then order_count DESC so the highest-order-count customer comes first per vehicle.
     */
    @Query(
            value =
                    """
            SELECT
                v.id            AS vehicle_id,
                v.license_plate,
                o.customer_id,
                c.customer_name,
                COUNT(DISTINCT od.order_code) AS order_count
            FROM vehicle v
            JOIN order_detail od ON od.vehicle_id = v.id
                AND od.delete_flag = false
            JOIN orders o ON o.code = od.order_code
                AND o.delete_flag = false
                AND o.customer_id IS NOT NULL
            JOIN customer c ON c.id = o.customer_id
                AND c.delete_flag = false
            WHERE v.customer_id IS NULL
                AND v.delete_flag = false
            GROUP BY v.id, v.license_plate, o.customer_id, c.customer_name
            ORDER BY v.id, order_count DESC
            """,
            nativeQuery = true)
    List<Object[]> findUnlinkedVehiclesWithCustomers();

    /** Count all vehicles with no owner (includes those with no orders). */
    long countByCustomerIsNullAndDeleteFlagFalse();

    /**
     * Bulk-link a single unlinked vehicle to a customer via native SQL.
     * The WHERE guard (customer_id IS NULL) prevents accidental overwrite if another
     * thread raced ahead of us.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value =
                    """
            UPDATE vehicle
            SET customer_id = :customerId,
                updated_at  = NOW()
            WHERE id            = :vehicleId
              AND customer_id   IS NULL
              AND delete_flag   = false
            """,
            nativeQuery = true)
    int linkVehicleToCustomer(@Param("vehicleId") UUID vehicleId, @Param("customerId") UUID customerId);
}
