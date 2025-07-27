package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_REVENUE;

import com.alphawash.dto.FavoriteServiceRevenueDto;
import com.alphawash.dto.RevenueDto;
import com.alphawash.request.FavoriteServiceRevenueRequest;
import com.alphawash.request.RevenueRequest;
import com.alphawash.service.RevenueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_REVENUE)
@RequiredArgsConstructor
@Tag(name = "Revenue", description = "List Revenue")
public class RevenueController {

    private final RevenueService revenueService;

    @PostMapping
    public ResponseEntity<List<RevenueDto>> getRevenue(@RequestBody RevenueRequest request) {
        var result = revenueService.getRevenue(request.startDate(), request.endDate(), request.orderStatus());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/favorite")
    public ResponseEntity<List<FavoriteServiceRevenueDto>> getFavoriteServiceRevenue(
            @RequestBody FavoriteServiceRevenueRequest request) {
        var result = revenueService.getFavoriteServiceRevenue(request.startDate(), request.endDate());
        return ResponseEntity.ok(result);
    }
}
