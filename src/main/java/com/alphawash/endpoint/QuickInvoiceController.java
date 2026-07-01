package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_QUICK_INVOICE;

import com.alphawash.dto.QuickServiceGroupDto;
import com.alphawash.dto.RecentVehicleDto;
import com.alphawash.service.QuickInvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_QUICK_INVOICE)
@RequiredArgsConstructor
@Tag(name = "Quick Invoice", description = "Quick Invoice Mode endpoints")
public class QuickInvoiceController {

    private final QuickInvoiceService quickInvoiceService;

    @GetMapping("/services")
    public ResponseEntity<List<QuickServiceGroupDto>> getGroupedServices() {
        return ResponseEntity.ok(quickInvoiceService.getGroupedServices());
    }

    @GetMapping("/recent-vehicles")
    public ResponseEntity<List<RecentVehicleDto>> getRecentVehicles(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quickInvoiceService.getRecentVehicles(limit));
    }
}
