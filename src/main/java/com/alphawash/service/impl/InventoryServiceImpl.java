package com.alphawash.service.impl;

import com.alphawash.constant.InventoryTransactionType;
import com.alphawash.dto.InventoryTransactionDto;
import com.alphawash.entity.InventoryTransaction;
import com.alphawash.entity.Product;
import com.alphawash.repository.InventoryTransactionRepository;
import com.alphawash.repository.ProductRepository;
import com.alphawash.request.InventoryAdjustRequest;
import com.alphawash.service.InventoryService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Override
    public List<InventoryTransactionDto> getAll() {
        return transactionRepository.findByDeleteFlagFalseOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<InventoryTransactionDto> getByProductCode(String productCode) {
        return transactionRepository.findByProduct_CodeAndDeleteFlagFalseOrderByCreatedAtDesc(productCode).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public InventoryTransactionDto adjust(InventoryAdjustRequest request) {
        Product product = productRepository.findByCode(request.getProductCode())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.getProductCode()));

        InventoryTransactionType type = InventoryTransactionType.valueOf(request.getType());
        int beforeQty = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
        int quantity = request.getQuantity();
        int afterQty;

        switch (type) {
            case STOCK_IN, PURCHASE_RECEIVE, RETURN -> afterQty = beforeQty + quantity;
            case STOCK_OUT, SALE -> afterQty = beforeQty - quantity;
            case ADJUSTMENT -> afterQty = quantity;
            default -> afterQty = beforeQty;
        }

        product.setCurrentStock(afterQty);
        productRepository.save(product);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setCode(generateCode());
        tx.setProduct(product);
        tx.setQuantity(type == InventoryTransactionType.ADJUSTMENT ? afterQty - beforeQty : quantity);
        tx.setBeforeQty(beforeQty);
        tx.setAfterQty(afterQty);
        tx.setType(type);
        tx.setReferenceNumber(request.getReferenceNumber());
        tx.setNotes(request.getNotes());

        return toDto(transactionRepository.save(tx));
    }

    @Override
    @Transactional
    public void deductStock(String productCode, int quantity, String referenceNumber) {
        InventoryAdjustRequest request = new InventoryAdjustRequest();
        request.setProductCode(productCode);
        request.setQuantity(quantity);
        request.setType(InventoryTransactionType.SALE.name());
        request.setReferenceNumber(referenceNumber);
        adjust(request);
    }

    @Override
    @Transactional
    public void addStock(String productCode, int quantity, String referenceNumber) {
        InventoryAdjustRequest request = new InventoryAdjustRequest();
        request.setProductCode(productCode);
        request.setQuantity(quantity);
        request.setType(InventoryTransactionType.PURCHASE_RECEIVE.name());
        request.setReferenceNumber(referenceNumber);
        adjust(request);
    }

    @Override
    public long getTotalProducts() {
        return productRepository.countByDeleteFlagFalse();
    }

    @Override
    public long getLowStockCount() {
        return productRepository.countLowStock();
    }

    @Override
    public long getOutOfStockCount() {
        return productRepository.countOutOfStock();
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        return productRepository.calculateTotalInventoryValue();
    }

    private String generateCode() {
        String datePrefix = "IT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        long count = transactionRepository.countByCodeStartingWith(datePrefix);
        return String.format("%s-%04d", datePrefix, count + 1);
    }

    private InventoryTransactionDto toDto(InventoryTransaction entity) {
        return InventoryTransactionDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .productCode(entity.getProduct() != null ? entity.getProduct().getCode() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getProductName() : null)
                .quantity(entity.getQuantity())
                .beforeQty(entity.getBeforeQty())
                .afterQty(entity.getAfterQty())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .referenceNumber(entity.getReferenceNumber())
                .notes(entity.getNotes())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
