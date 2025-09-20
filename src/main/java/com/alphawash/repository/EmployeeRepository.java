package com.alphawash.repository;

import com.alphawash.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findById(Long id);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM employee e "
                    + "WHERE e.phone = :phone",
            nativeQuery = true)
    boolean existsByPhone(@Param("phone") String phone);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM employee e "
                    + "WHERE e.identity_number = :identityNumber",
            nativeQuery = true)
    boolean existsByIdentityNumber(@Param("identityNumber") String identityNumber);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM employee e "
                    + "WHERE e.phone = :phone "
                    + "AND e.id <> :id",
            nativeQuery = true)
    boolean existsDuplicatePhone(@Param("phone") String phone, @Param("id") Long id);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM employee e "
                    + "WHERE e.identity_number = :identityNumber "
                    + "AND e.id <> :id",
            nativeQuery = true)
    boolean existsByIdentityNumberAndIdNot(@Param("identityNumber") String identityNumber, @Param("id") Long id);
}
