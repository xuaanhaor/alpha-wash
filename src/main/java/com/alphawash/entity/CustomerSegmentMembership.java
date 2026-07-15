package com.alphawash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "customer_segment_membership",
       uniqueConstraints = @UniqueConstraint(columnNames = {"segment_code", "customer_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegmentMembership extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_code", nullable = false)
    private String segmentCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;
}
