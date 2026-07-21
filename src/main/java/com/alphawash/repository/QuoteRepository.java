package com.alphawash.repository;

import com.alphawash.constant.QuoteStatus;
import com.alphawash.entity.Quote;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    @Query(
            """
            SELECT q FROM Quote q
            WHERE q.deleteFlag = false
            AND (:search IS NULL
                 OR LOWER(q.customerName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR LOWER(q.customerPhone) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR LOWER(q.quoteCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR LOWER(q.licensePlate) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
            AND (:status IS NULL OR q.status = :status)
            ORDER BY q.createdAt DESC
            """)
    Page<Quote> search(
            @Param("search") String search, @Param("status") QuoteStatus status, Pageable pageable);

    Optional<Quote> findByIdAndDeleteFlagFalse(UUID id);

    /** Đếm số báo giá tạo trong ngày để sinh quoteCode */
    @Query(value = "SELECT COUNT(*) FROM quote WHERE DATE(created_at) = :date AND delete_flag = false", nativeQuery = true)
    long countByCreatedDate(@Param("date") LocalDate date);
}
