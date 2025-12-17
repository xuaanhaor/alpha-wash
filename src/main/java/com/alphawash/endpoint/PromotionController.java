package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionStatus;
import com.alphawash.request.AddPromotionServicesRequest;
import com.alphawash.request.PromotionCreateRequest;
import com.alphawash.response.AvailablePromotionResponse;
import com.alphawash.response.PromotionResponse;
import com.alphawash.service.PromotionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.alphawash.constant.Constant.API_PROMOTION;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PROMOTION)
@Tag(name = "Promotion", description = "Manager Promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping(Constant.SEARCH_ENDPOINT)
    public ResponseEntity<Page<PromotionResponse>> getAllPromotion (
            @RequestParam(required = false) PromotionStatus status,
            @RequestParam(required = false) PromoType promoType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PromotionResponse> response = promotionService.getAll(status, promoType, keyword, fromDate, toDate, pageable);
        if (response == null || response.toString().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(Constant.API_CREATE_ORDER)
    public ResponseEntity<PromotionResponse> createPromotion(@RequestBody PromotionCreateRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        if (response == null || response.toString().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{promotionId}/services")
    public ResponseEntity<Void> addServices(@PathVariable UUID promotionId,
                            @RequestBody List<AddPromotionServicesRequest> request) {
        promotionService.addPromotionServices(promotionId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active-for-order/{customerId}")
    public ResponseEntity<List<AvailablePromotionResponse>> getActiveForOrder(@PathVariable UUID customerId) {
        List<AvailablePromotionResponse> response = promotionService.getActivePromotionsForOrder(customerId);
        if (response == null || response.toString().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }
}
