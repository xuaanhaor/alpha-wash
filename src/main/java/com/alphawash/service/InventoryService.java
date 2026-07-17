package com.alphawash.service;

import com.alphawash.dto.InventoryTransactionDto;
import com.alphawash.request.InventoryAdjustRequest;
import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {
    List<InventoryTransactionDto> getAll();

    List<InventoryTransactionDto> getByProductCode(String productCode);

    InventoryTransactionDto adjust(InventoryAdjustRequest request);

    void deductStock(String productCode, int quantity, String referenceNumber);

    void addStock(String productCode, int quantity, String referenceNumber);

    long getTotalProducts();

    long getLowStockCount();

    long getOutOfStockCount();

    BigDecimal getTotalInventoryValue();
}
