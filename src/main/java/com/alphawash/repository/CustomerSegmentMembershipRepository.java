package com.alphawash.repository;

import com.alphawash.entity.CustomerSegmentMembership;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerSegmentMembershipRepository extends JpaRepository<CustomerSegmentMembership, Long> {

    List<CustomerSegmentMembership> findByCustomerId(UUID customerId);

    List<CustomerSegmentMembership> findByCustomerIdIn(List<UUID> customerIds);

    List<CustomerSegmentMembership> findBySegmentCode(String segmentCode);

    @Query("SELECT m.customerId FROM CustomerSegmentMembership m WHERE m.segmentCode = :segmentCode")
    List<UUID> findCustomerIdsBySegmentCode(String segmentCode);

    @Modifying
    @Query("DELETE FROM CustomerSegmentMembership m WHERE m.segmentCode = :segmentCode")
    void deleteBySegmentCode(String segmentCode);

    @Query("SELECT COUNT(m) FROM CustomerSegmentMembership m WHERE m.segmentCode = :segmentCode")
    int countBySegmentCode(String segmentCode);
}
