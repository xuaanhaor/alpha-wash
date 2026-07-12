package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_DASHBOARD;

import com.alphawash.dto.DashboardDto;
import com.alphawash.request.DashboardRequest;
import com.alphawash.service.DashboardService;
import com.alphawash.util.DateTimeUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_DASHBOARD)
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Executive Dashboard API")
public class DashboardController {

    private final DashboardService dashboardService;

    @PostMapping
    public ResponseEntity<DashboardDto> getDashboard(@RequestBody DashboardRequest request) {
        var start = DateTimeUtils.convertToLocalDate(request.startDate());
        var end = DateTimeUtils.convertToLocalDate(request.endDate());
        var compareStart = DateTimeUtils.convertToLocalDate(request.compareStartDate());
        var compareEnd = DateTimeUtils.convertToLocalDate(request.compareEndDate());

        var result = dashboardService.getDashboard(start, end, compareStart, compareEnd);
        return ResponseEntity.ok(result);
    }
}
