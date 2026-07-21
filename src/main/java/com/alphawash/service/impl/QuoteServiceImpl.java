package com.alphawash.service.impl;

import com.alphawash.constant.QuoteStatus;
import com.alphawash.entity.Quote;
import com.alphawash.entity.QuoteItem;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.QuoteItemRepository;
import com.alphawash.repository.QuoteRepository;
import com.alphawash.request.QuoteItemRequest;
import com.alphawash.request.QuoteRequest;
import com.alphawash.request.QuoteStatusRequest;
import com.alphawash.response.PageResponse;
import com.alphawash.response.QuoteItemResponse;
import com.alphawash.response.QuoteResponse;
import com.alphawash.service.QuoteService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteItemRepository quoteItemRepository;

    @Override
    public PageResponse<QuoteResponse> list(String search, QuoteStatus status, int page, int size) {
        Page<Quote> paged = quoteRepository.search(
                (search != null && search.isBlank()) ? null : search, status, PageRequest.of(page, size));
        List<QuoteResponse> content = paged.getContent().stream()
                .map(q -> toResponse(q, false))
                .toList();
        return PageResponse.<QuoteResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(paged.getTotalElements())
                .totalPages(paged.getTotalPages())
                .build();
    }

    @Override
    public QuoteResponse getById(UUID id) {
        Quote quote = findOrThrow(id);
        return toResponse(quote, true);
    }

    @Override
    @Transactional
    public QuoteResponse create(QuoteRequest request) {
        String code = generateQuoteCode();
        Quote quote = Quote.builder()
                .quoteCode(code)
                .customerId(request.customerId())
                .customerName(request.customerName())
                .customerPhone(request.customerPhone())
                .vehicleId(request.vehicleId())
                .licensePlate(request.licensePlate())
                .carModel(request.carModel())
                .carSize(request.carSize())
                .quoteDate(request.quoteDate() != null ? request.quoteDate() : LocalDate.now())
                .status(QuoteStatus.DRAFT)
                .discount(request.discount() != null ? request.discount() : BigDecimal.ZERO)
                .extraCharge(request.extraCharge() != null ? request.extraCharge() : BigDecimal.ZERO)
                .notes(request.notes())
                .build();

        List<QuoteItem> items = buildItems(quote, request.items());
        quote.setItems(items);
        recalculate(quote);

        return toResponse(quoteRepository.save(quote), true);
    }

    @Override
    @Transactional
    public QuoteResponse update(UUID id, QuoteRequest request) {
        Quote quote = findOrThrow(id);
        if (quote.getStatus() == QuoteStatus.ACCEPTED || quote.getStatus() == QuoteStatus.REJECTED) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "Không thể sửa báo giá ở trạng thái " + quote.getStatus());
        }

        if (request.customerId() != null) quote.setCustomerId(request.customerId());
        if (request.customerName() != null) quote.setCustomerName(request.customerName());
        if (request.customerPhone() != null) quote.setCustomerPhone(request.customerPhone());
        if (request.vehicleId() != null) quote.setVehicleId(request.vehicleId());
        if (request.licensePlate() != null) quote.setLicensePlate(request.licensePlate());
        if (request.carModel() != null) quote.setCarModel(request.carModel());
        if (request.carSize() != null) quote.setCarSize(request.carSize());
        if (request.quoteDate() != null) quote.setQuoteDate(request.quoteDate());
        if (request.discount() != null) quote.setDiscount(request.discount());
        if (request.extraCharge() != null) quote.setExtraCharge(request.extraCharge());
        if (request.notes() != null) quote.setNotes(request.notes());

        if (request.items() != null) {
            quote.getItems().clear();
            quote.getItems().addAll(buildItems(quote, request.items()));
        }
        recalculate(quote);

        return toResponse(quoteRepository.save(quote), true);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Quote quote = findOrThrow(id);
        quote.setDeleteFlag(true);
        quoteRepository.save(quote);
    }

    @Override
    @Transactional
    public QuoteResponse updateStatus(UUID id, QuoteStatusRequest request) {
        if (request.status() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Trạng thái không được để trống");
        }
        Quote quote = findOrThrow(id);
        quote.setStatus(request.status());
        return toResponse(quoteRepository.save(quote), true);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Quote findOrThrow(UUID id) {
        return quoteRepository
                .findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy báo giá: " + id));
    }

    private List<QuoteItem> buildItems(Quote quote, List<QuoteItemRequest> itemRequests) {
        if (itemRequests == null) return new ArrayList<>();
        return itemRequests.stream()
                .map(req -> QuoteItem.builder()
                        .quote(quote)
                        .serviceId(req.serviceId())
                        .serviceName(req.serviceName())
                        .brand(req.brand())
                        .typeDetail(req.typeDetail())
                        .warranty(req.warranty())
                        .price(req.price() != null ? req.price() : BigDecimal.ZERO)
                        .bonus(req.bonus())
                        .sortOrder(req.sortOrder())
                        .build())
                .toList();
    }

    /**
     * subtotal = sum of non-bonus items
     * total = subtotal - discount + extraCharge
     */
    private void recalculate(Quote quote) {
        BigDecimal subtotal = quote.getItems().stream()
                .filter(i -> !i.isBonus())
                .map(QuoteItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        quote.setSubtotal(subtotal);
        BigDecimal discount = quote.getDiscount() != null ? quote.getDiscount() : BigDecimal.ZERO;
        BigDecimal extra = quote.getExtraCharge() != null ? quote.getExtraCharge() : BigDecimal.ZERO;
        quote.setTotal(subtotal.subtract(discount).add(extra));
    }

    private String generateQuoteCode() {
        LocalDate today = LocalDate.now();
        long count = quoteRepository.countByCreatedDate(today);
        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "QT-" + datePart + "-" + String.format("%03d", count + 1);
    }

    private QuoteResponse toResponse(Quote q, boolean includeItems) {
        QuoteResponse.QuoteResponseBuilder builder = QuoteResponse.builder()
                .id(q.getId())
                .quoteCode(q.getQuoteCode())
                .customerId(q.getCustomerId())
                .customerName(q.getCustomerName())
                .customerPhone(q.getCustomerPhone())
                .vehicleId(q.getVehicleId())
                .licensePlate(q.getLicensePlate())
                .carModel(q.getCarModel())
                .carSize(q.getCarSize())
                .quoteDate(q.getQuoteDate())
                .status(q.getStatus())
                .subtotal(q.getSubtotal())
                .discount(q.getDiscount())
                .extraCharge(q.getExtraCharge())
                .total(q.getTotal())
                .notes(q.getNotes())
                .createdBy(q.getCreatedBy())
                .createdAt(q.getCreatedAt());

        if (includeItems) {
            List<QuoteItemResponse> itemResponses = q.getItems().stream()
                    .map(i -> QuoteItemResponse.builder()
                            .id(i.getId())
                            .serviceId(i.getServiceId())
                            .serviceName(i.getServiceName())
                            .brand(i.getBrand())
                            .typeDetail(i.getTypeDetail())
                            .warranty(i.getWarranty())
                            .price(i.getPrice())
                            .bonus(i.isBonus())
                            .sortOrder(i.getSortOrder())
                            .build())
                    .toList();
            builder.items(itemResponses);
        }
        return builder.build();
    }
}
