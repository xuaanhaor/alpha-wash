package com.alphawash.service.impl;

import com.alphawash.constant.PurchaseOrderStatus;
import com.alphawash.dto.PurchaseOrderDto;
import com.alphawash.dto.PurchaseOrderItemDto;
import com.alphawash.entity.Product;
import com.alphawash.entity.PurchaseOrder;
import com.alphawash.entity.PurchaseOrderItem;
import com.alphawash.entity.Supplier;
import com.alphawash.repository.ProductRepository;
import com.alphawash.repository.PurchaseOrderItemRepository;
import com.alphawash.repository.PurchaseOrderRepository;
import com.alphawash.repository.SupplierRepository;
import com.alphawash.request.PurchaseOrderItemRequest;
import com.alphawash.request.PurchaseOrderReceiveRequest;
import com.alphawash.request.PurchaseOrderRequest;
import com.alphawash.service.InventoryService;
import com.alphawash.service.PurchaseOrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository repository;
    private final PurchaseOrderItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    public List<PurchaseOrderDto> getAll() {
        return repository.findByDeleteFlagFalseOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public PurchaseOrderDto getByCode(String code) {
        return repository.findByCode(code).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public PurchaseOrderDto create(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findByCode(request.getSupplierCode()).orElse(null);

        PurchaseOrder po = new PurchaseOrder();
        po.setCode(generateCode());
        po.setSupplier(supplier);
        po.setPurchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDateTime.now());
        po.setInvoiceNumber(request.getInvoiceNumber());
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        PurchaseOrder savedPo = repository.save(po);

        if (request.getItems() != null) {
            for (PurchaseOrderItemRequest itemReq : request.getItems()) {
                Product product = productRepository.findByCode(itemReq.getProductCode()).orElse(null);
                BigDecimal totalCost = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(savedPo);
                item.setProduct(product);
                item.setQuantity(itemReq.getQuantity());
                item.setUnitCost(itemReq.getUnitCost());
                item.setTotalCost(totalCost);
                item.setReceivedQuantity(0);
                itemRepository.save(item);

                totalAmount = totalAmount.add(totalCost);
            }
        }

        savedPo.setTotalAmount(totalAmount);
        return toDto(repository.save(savedPo));
    }

    @Override
    @Transactional
    public PurchaseOrderDto update(String code, PurchaseOrderRequest request) {
        return repository.findByCode(code).map(po -> {
            if (request.getSupplierCode() != null) {
                Supplier supplier = supplierRepository.findByCode(request.getSupplierCode()).orElse(null);
                po.setSupplier(supplier);
            }
            if (request.getPurchaseDate() != null) po.setPurchaseDate(request.getPurchaseDate());
            if (request.getInvoiceNumber() != null) po.setInvoiceNumber(request.getInvoiceNumber());
            if (request.getNotes() != null) po.setNotes(request.getNotes());
            if (request.getStatus() != null) po.setStatus(PurchaseOrderStatus.valueOf(request.getStatus()));
            return toDto(repository.save(po));
        }).orElse(null);
    }

    @Override
    @Transactional
    public PurchaseOrderDto receive(String code, PurchaseOrderReceiveRequest request) {
        PurchaseOrder po = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("PO not found: " + code));

        List<PurchaseOrderItem> items = itemRepository.findByPurchaseOrder_CodeAndDeleteFlagFalse(code);
        boolean allReceived = true;

        for (PurchaseOrderReceiveRequest.ReceiveItem receiveItem : request.getItems()) {
            PurchaseOrderItem poItem = items.stream()
                    .filter(i -> i.getProduct() != null && i.getProduct().getCode().equals(receiveItem.getProductCode()))
                    .findFirst()
                    .orElse(null);

            if (poItem != null && receiveItem.getReceivedQuantity() > 0) {
                int newReceived = poItem.getReceivedQuantity() + receiveItem.getReceivedQuantity();
                poItem.setReceivedQuantity(newReceived);
                itemRepository.save(poItem);

                inventoryService.addStock(
                        receiveItem.getProductCode(),
                        receiveItem.getReceivedQuantity(),
                        po.getCode()
                );

                if (newReceived < poItem.getQuantity()) {
                    allReceived = false;
                }
            }
        }

        po.setStatus(allReceived ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIAL_RECEIVED);
        return toDto(repository.save(po));
    }

    @Override
    @Transactional
    public void cancel(String code) {
        repository.findByCode(code).ifPresent(po -> {
            po.setStatus(PurchaseOrderStatus.CANCELLED);
            repository.save(po);
        });
    }

    private String generateCode() {
        String datePrefix = "PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        long count = repository.countByCodeStartingWith(datePrefix);
        return String.format("%s-%03d", datePrefix, count + 1);
    }

    private PurchaseOrderDto toDto(PurchaseOrder entity) {
        List<PurchaseOrderItemDto> items = itemRepository
                .findByPurchaseOrder_CodeAndDeleteFlagFalse(entity.getCode())
                .stream()
                .map(item -> PurchaseOrderItemDto.builder()
                        .id(item.getId())
                        .productCode(item.getProduct() != null ? item.getProduct().getCode() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getProductName() : null)
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitCost())
                        .totalCost(item.getTotalCost())
                        .receivedQuantity(item.getReceivedQuantity())
                        .build())
                .toList();

        return PurchaseOrderDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .supplierCode(entity.getSupplier() != null ? entity.getSupplier().getCode() : null)
                .supplierName(entity.getSupplier() != null ? entity.getSupplier().getSupplierName() : null)
                .purchaseDate(entity.getPurchaseDate())
                .invoiceNumber(entity.getInvoiceNumber())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .notes(entity.getNotes())
                .items(items)
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .exclusiveKey(entity.getExclusiveKey())
                .build();
    }
}
