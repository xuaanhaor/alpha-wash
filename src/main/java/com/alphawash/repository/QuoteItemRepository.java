package com.alphawash.repository;

import com.alphawash.entity.QuoteItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, UUID> {

    List<QuoteItem> findByQuoteIdOrderBySortOrderAsc(UUID quoteId);

    void deleteByQuoteId(UUID quoteId);
}
