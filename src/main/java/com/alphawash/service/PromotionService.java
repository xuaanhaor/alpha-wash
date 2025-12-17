package com.alphawash.service;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionStatus;
import com.alphawash.request.AddPromotionServicesRequest;
import com.alphawash.request.PromotionCreateRequest;
import com.alphawash.response.AvailablePromotionResponse;
import com.alphawash.response.PromotionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PromotionService {
    Page<PromotionResponse> getAll(PromotionStatus status, PromoType promoType, String keyword, LocalDate fromDate,
                                   LocalDate toDate,
                                   Pageable pageable);
    PromotionResponse createPromotion(PromotionCreateRequest req);
    void addPromotionServices(UUID promotionId, List<AddPromotionServicesRequest> request);
    List<AvailablePromotionResponse> getActivePromotionsForOrder(UUID customerId);
}
