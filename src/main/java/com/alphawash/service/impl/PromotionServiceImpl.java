package com.alphawash.service.impl;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionStatus;
import com.alphawash.entity.Promotion;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.CustomerPromotionRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.PromotionRepository;
import com.alphawash.repository.PromotionServiceRepository;
import com.alphawash.request.AddPromotionServicesRequest;
import com.alphawash.request.PromotionCreateRequest;
import com.alphawash.response.AddPromotionServicesResponse;
import com.alphawash.response.AvailablePromotionResponse;
import com.alphawash.response.PromotionResponse;
import com.alphawash.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionServiceRepository promotionServiceRepository;
    private final CustomerPromotionRepository customerPromotionRepository;
    private final OrderRepository orderRepository;

    @Override
    public Page<PromotionResponse> getAll(PromotionStatus status, PromoType promoType, String keyword, LocalDate fromDate,
                                          LocalDate toDate,
                                          Pageable pageable) {

        // 1) Lấy danh sách promotion
        LocalDateTime fromTs = (fromDate == null) ? null : fromDate.atStartOfDay();
        // toDate inclusive -> convert thành cuối ngày (23:59:59.999999999)
        LocalDateTime toTs = (toDate == null) ? null : toDate.atTime(23, 59, 59);

        Page<Promotion> page = promotionRepository.findAllFilter(
                status, promoType, keyword, fromTs, toTs, pageable
        );

        List<Promotion> promotions = page.getContent();
        if (promotions.isEmpty()) {
            return page.map(p -> null); // hoặc return Page.empty(pageable);
        }

        // 2) Lấy các promotionId thuộc loại SERVICE_*
        List<UUID> servicePromotionIds = promotions.stream()
                .filter(p -> p.getPromoType() == PromoType.SERVICE_PERCENT
                        || p.getPromoType() == PromoType.SERVICE_AMOUNT)
                .map(Promotion::getId)
                .toList();

        // 3) Lấy map promotionId -> List<AddPromotionServicesRequest>
        Map<UUID, List<AddPromotionServicesResponse>> servicesMap =
                fetchPromotionServicesMap(servicePromotionIds);

        // 4) Build response
        return page.map(p -> PromotionResponse.builder()
                .id(p.getId())
                .promoCode(p.getPromoCode())
                .promoName(p.getPromoName())
                .promoType(p.getPromoType())
                .value(p.getValue())
                .usageLimit(p.getUsageLimit())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .description(p.getDescription())
                .status(p.getStatus())
                .promotionMethod(p.getPromotionMethod())
                .campaignLink(p.getCampaignLink())
                .targetAudience(p.getTargetAudience())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .services(servicesMap.getOrDefault(p.getId(), List.of()))
                .build());
    }

    private Map<UUID, List<AddPromotionServicesResponse>> fetchPromotionServicesMap(List<UUID> promotionIds) {

        if (promotionIds == null || promotionIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> rows = promotionServiceRepository.findServiceItemsWithNameByPromotionIds(promotionIds);

        Map<UUID, List<AddPromotionServicesResponse>> map = new HashMap<>();

        for (Object[] row : rows) {
            UUID promotionId = (UUID) row[0];
            String serviceCode = (String) row[1];
            String serviceName = (String) row[2];
            BigDecimal discountAmount = (BigDecimal) row[3];
            BigDecimal discountPercent = (BigDecimal) row[4];

            AddPromotionServicesResponse serviceItem = new AddPromotionServicesResponse();
            serviceItem.setServiceCode(serviceCode);
            serviceItem.setServiceName(serviceName);
            serviceItem.setDiscountAmount(discountAmount);
            serviceItem.setDiscountPercent(discountPercent);

            map.computeIfAbsent(promotionId, k -> new ArrayList<>())
                    .add(serviceItem);
        }

        return map;
    }


    @Transactional
    @Override
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        // 1. Validate cơ bản
        if (request.promoCode() == null || request.promoCode().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Mã code không được để trống");
        }
        if (request.promoName() == null || request.promoName().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tên chương trình không được để trống");
        }
        if (request.promoType() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Loại chương trình không được để trống");
        }
        if (request.value() == null || request.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Giá trị khuyến mãi phải lớn hơn 0");
        }

        // Không cho trùng mã
        if (promotionRepository.existsByPromoCode(request.promoCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Mã code đã tồn tại");
        }

        // 2. Tạo entity Promotion
        Promotion p = new Promotion();
        p.setPromoCode(request.promoCode());
        p.setPromoName(request.promoName());
        p.setPromoType(request.promoType());
        p.setValue(request.value());
        p.setUsageLimit(request.usageLimit());

        p.setStartDate(request.startDate());
        p.setEndDate(request.endDate());
        p.setDescription(request.description());

        p.setPromotionMethod(request.promotionMethod());
        p.setCampaignLink(request.campaignLink());
        p.setTargetAudience(request.targetAudience());

        // BILL_* bắt buộc value
        if (request.promoType() == PromoType.BILL_AMOUNT || request.promoType() == PromoType.BILL_PERCENT) {
            if (request.value() == null || request.value().compareTo(BigDecimal.ZERO) <= 0)
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Giá trị này là bắt buộc đối với các chương trình khuyến mãi HÓA ĐƠN.");
            p.setValue(request.value());
        } else {
            // SERVICE_*: value có thể null
            p.setValue(request.value());
        }

        // status
        PromotionStatus st = request.status();
        if (st == null) {
            LocalDateTime now = LocalDateTime.now();
            st = (request.startDate() != null && request.startDate().isAfter(now))
                    ? PromotionStatus.SCHEDULED
                    : PromotionStatus.ACTIVE;
        }
        p.setStatus(st);

        Promotion saved = promotionRepository.save(p);

        // 6. Trả về kết quả
        PromotionResponse response = new PromotionResponse();
        response.setId(saved.getId());
        response.setPromoCode(saved.getPromoCode());
        response.setPromoName(saved.getPromoName());
        response.setPromoType(saved.getPromoType());
        response.setValue(saved.getValue());
        response.setUsageLimit(saved.getUsageLimit());
        response.setStartDate(saved.getStartDate());
        response.setEndDate(saved.getEndDate());
        response.setDescription(saved.getDescription());
        response.setStatus(saved.getStatus());
        response.setPromotionMethod(saved.getPromotionMethod());
        response.setCampaignLink(saved.getCampaignLink());
        response.setTargetAudience(saved.getTargetAudience());
        response.setCreatedBy(saved.getCreatedBy());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        return response;
    }


    @Transactional
    @Override
    public void addPromotionServices(UUID promotionId, List<AddPromotionServicesRequest> request) {

        Promotion promo = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy chương trình khuyến mãi này"));

        if (!(promo.getPromoType() == PromoType.SERVICE_AMOUNT || promo.getPromoType() == PromoType.SERVICE_PERCENT)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Chương trình khuyến mãi này không dựa trên dịch vụ");
        }

        if (request == null || request.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Yêu cầu bị Null");
        }

        for (AddPromotionServicesRequest item : request) {
            if (item.serviceCode() == null || item.serviceCode().isBlank())
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Service Code là bắt buộc");

            if (promo.getPromoType() == PromoType.SERVICE_AMOUNT) {
                if (item.discountAmount() == null || item.discountAmount().compareTo(BigDecimal.ZERO) <= 0)
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "discountAmount là bắt buộc cho loại KM SERVICE_AMOUNT");
            }

            if (promo.getPromoType() == PromoType.SERVICE_PERCENT) {
                if (item.discountPercent() == null || item.discountPercent().compareTo(BigDecimal.ZERO) <= 0)
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "discountPercent là bắt buộc cho loại KM SERVICE_PERCENT");
            }

            com.alphawash.entity.PromotionService ps = new com.alphawash.entity.PromotionService();
            ps.setPromotionId(promotionId);
            ps.setServiceCode(item.serviceCode());
            ps.setDiscountAmount(item.discountAmount());
            ps.setDiscountPercent(item.discountPercent());

            promotionServiceRepository.save(ps);
        }
    }

    @Transactional
    @Override
    public List<AvailablePromotionResponse> getActivePromotionsForOrder(UUID customerId) {
        if (customerId == null) return List.of();

        List<Promotion> promos = promotionRepository.findActiveNow();
        if (promos.isEmpty()) return List.of();

        // Lọc ra promo SERVICE_* để lấy danh sách service giảm giá theo promoId
        List<UUID> servicePromotionIds = promos.stream()
                .filter(p -> p.getPromoType() == PromoType.SERVICE_PERCENT
                        || p.getPromoType() == PromoType.SERVICE_AMOUNT)
                .map(Promotion::getId)
                .toList();

        // Map promoId -> list services (serviceCode, serviceName, discountAmount, discountPercent)
        Map<UUID, List<AddPromotionServicesResponse>> servicesMap =
                fetchPromotionServicesMap(servicePromotionIds);

        List<AvailablePromotionResponse> result = new ArrayList<>(promos.size());

        for (Promotion p : promos) {
            Eligibility e = checkEligible(p, customerId);

            result.add(AvailablePromotionResponse.builder()
                    .promoId(p.getId())
                    .promoCode(p.getPromoCode())
                    .promoName(p.getPromoName())
                    .promoType(p.getPromoType())
                    .value(p.getValue())
                    .startDate(p.getStartDate())
                    .endDate(p.getEndDate())
                    .usable(e.usable)
                    .reason(e.reason)
                    .services(servicesMap.getOrDefault(p.getId(), List.of()))
                    .build());
        }

        return result;
    }

    private Eligibility checkEligible(Promotion p, UUID customerId) {

        // Rule “khách cũ” dựa trên ngày bắt đầu CTKM
        if (requiresOldCustomer(p)) {
            if (p.getStartDate() == null) {
                return new Eligibility(false, "Khuyến mãi lỗi cấu hình: thiếu ngày bắt đầu");
            }

            // khách cũ = đã có đơn trước ngày startDate của promo
            boolean isOldCustomer = orderRepository.isOldCustomer(customerId, p.getStartDate());

            if (!isOldCustomer) {
                return new Eligibility(false, "Khách không phải khách cũ");
            }
        }

        // usageLimit (0 = vô hạn)
        int limit = p.getUsageLimit() == null ? 0 : p.getUsageLimit();
        if (limit > 0) {
            long used = customerPromotionRepository.countUsed(customerId, p.getId());
            if (used >= limit) return new Eligibility(false, "Đã vượt quá số lần sử dụng");
        }

        return new Eligibility(true, null);
    }

    private boolean requiresOldCustomer(Promotion p) {
        String audience = (p.getTargetAudience() == null) ? "" : p.getTargetAudience().toLowerCase();
        return audience.contains("khách cũ");
    }

    private static class Eligibility {
        boolean usable;
        String reason;

        Eligibility(boolean usable, String reason) {
            this.usable = usable;
            this.reason = reason;
        }
    }
}
