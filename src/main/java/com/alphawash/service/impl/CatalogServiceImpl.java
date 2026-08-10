package com.alphawash.service.impl;

import com.alphawash.constant.ServiceCategory;
import com.alphawash.entity.ServiceItem;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.ServiceItemRepository;
import com.alphawash.request.ServiceItemRequest;
import com.alphawash.response.ServiceItemResponse;
import com.alphawash.service.CatalogService;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final ServiceItemRepository serviceItemRepository;

    @Override
    public List<ServiceItemResponse> getAll(ServiceCategory category, boolean activeOnly) {
        List<ServiceItem> items;
        if (activeOnly) {
            if (category != null) {
                items = serviceItemRepository
                        .findByCategoryAndActiveTrueAndDeleteFlagFalseOrderBySortOrderAsc(category);
            } else {
                items = serviceItemRepository
                        .findByActiveTrueAndDeleteFlagFalseOrderByCategoryAscSortOrderAsc();
            }
        } else {
            if (category != null) {
                items = serviceItemRepository
                        .findByCategoryAndDeleteFlagFalseOrderBySortOrderAsc(category);
            } else {
                items = serviceItemRepository.findByDeleteFlagFalseOrderByCategoryAscSortOrderAsc();
            }
        }
        return items.stream().map(this::toResponse).toList();
    }

    @Override
    public List<ServiceCategory> getCategories() {
        return Arrays.stream(ServiceCategory.values()).toList();
    }

    @Override
    public List<ServiceItemResponse> getBonusServices() {
        return serviceItemRepository.findByCanBeBonusTrueAndActiveTrueAndDeleteFlagFalse().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ServiceItemResponse getById(UUID id) {
        return serviceItemRepository
                .findById(id)
                .filter(s -> !s.getDeleteFlag())
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ: " + id));
    }

    @Override
    @Transactional
    public ServiceItemResponse create(ServiceItemRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tên dịch vụ không được để trống");
        }
        if (request.category() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Danh mục không được để trống");
        }
        ServiceItem item = buildFromRequest(new ServiceItem(), request);
        return toResponse(serviceItemRepository.save(item));
    }

    @Override
    @Transactional
    public ServiceItemResponse update(UUID id, ServiceItemRequest request) {
        ServiceItem item = serviceItemRepository
                .findById(id)
                .filter(s -> !s.getDeleteFlag())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ: " + id));
        buildFromRequest(item, request);
        return toResponse(serviceItemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ServiceItem item = serviceItemRepository
                .findById(id)
                .filter(s -> !s.getDeleteFlag())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ: " + id));
        item.setDeleteFlag(true);
        item.setActive(false);
        serviceItemRepository.save(item);
    }

    @Override
    @Transactional
    public int backfillCategoryCode() {
        List<ServiceItem> needsBackfill = serviceItemRepository
                .findByDeleteFlagFalseOrderByCategoryAscSortOrderAsc()
                .stream()
                .filter(s -> s.getCategoryCode() == null || s.getCategoryCode().isBlank())
                .toList();

        for (ServiceItem item : needsBackfill) {
            // Use the stored enum name as the category code so toResponse stays consistent
            if (item.getCategory() != null) {
                item.setCategoryCode(item.getCategory().name());
            }
        }
        if (!needsBackfill.isEmpty()) {
            serviceItemRepository.saveAll(needsBackfill);
        }
        return needsBackfill.size();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    /**
     * Parse category string → enum với fallback OTHER.
     * Luôn set categoryCode để giữ nguyên code gốc (kể cả custom code).
     */
    private void applyCategory(ServiceItem item, String categoryStr) {
        if (categoryStr == null) return;
        String code = categoryStr.trim().toUpperCase();
        ServiceCategory enumCat;
        try {
            enumCat = ServiceCategory.valueOf(code);
        } catch (IllegalArgumentException e) {
            // Custom dynamic category không có trong enum → lưu vào OTHER + categoryCode
            enumCat = ServiceCategory.OTHER;
        }
        item.setCategory(enumCat);
        item.setCategoryCode(code);
    }

    private ServiceItem buildFromRequest(ServiceItem item, ServiceItemRequest req) {
        if (req.name() != null) item.setName(req.name());
        if (req.category() != null) applyCategory(item, req.category());
        if (req.brand() != null) item.setBrand(req.brand());
        if (req.typeDetail() != null) item.setTypeDetail(req.typeDetail());
        if (req.warranty() != null) item.setWarranty(req.warranty());
        if (req.priceS() != null) item.setPriceS(req.priceS());
        if (req.priceM() != null) item.setPriceM(req.priceM());
        if (req.priceL() != null) item.setPriceL(req.priceL());
        if (req.priceSEDAN() != null) item.setPriceSEDAN(req.priceSEDAN());
        if (req.priceSUV() != null) item.setPriceSUV(req.priceSUV());
        if (req.priceOverSize() != null) item.setPriceOverSize(req.priceOverSize());
        if (req.canBeBonus() != null) item.setCanBeBonus(req.canBeBonus());
        if (req.active() != null) item.setActive(req.active());
        if (req.description() != null) item.setDescription(req.description());
        if (req.sortOrder() != null) item.setSortOrder(req.sortOrder());
        return item;
    }

    private ServiceItemResponse toResponse(ServiceItem s) {
        // Ưu tiên categoryCode (dynamic string), fallback về enum name
        String categoryStr = s.getCategoryCode() != null
                ? s.getCategoryCode()
                : (s.getCategory() != null ? s.getCategory().name() : null);
        return ServiceItemResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .category(categoryStr)
                .categoryCode(s.getCategoryCode())
                .brand(s.getBrand())
                .typeDetail(s.getTypeDetail())
                .warranty(s.getWarranty())
                .priceS(s.getPriceS())
                .priceM(s.getPriceM())
                .priceL(s.getPriceL())
                .priceSEDAN(s.getPriceSEDAN())
                .priceSUV(s.getPriceSUV())
                .priceOverSize(s.getPriceOverSize())
                .canBeBonus(s.isCanBeBonus())
                .active(s.isActive())
                .description(s.getDescription())
                .sortOrder(s.getSortOrder())
                .build();
    }
}
