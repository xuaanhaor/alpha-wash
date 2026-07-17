package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.InventoryTransactionDto;
import com.alphawash.request.InventoryAdjustRequest;
import com.alphawash.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_INVENTORY)
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Manage product inventory and stock transactions")
public class InventoryController {

    private final InventoryService service;

    @Operation(summary = "Get all inventory transactions")
    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransactionDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get transactions by product code")
    @GetMapping("/transactions/product/{productCode}")
    public ResponseEntity<List<InventoryTransactionDto>> getByProductCode(@PathVariable String productCode) {
        return ResponseEntity.ok(service.getByProductCode(productCode));
    }

    @Operation(summary = "Adjust stock")
    @PostMapping("/adjust")
    public ResponseEntity<InventoryTransactionDto> adjust(@RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(service.adjust(request));
    }

    @Operation(summary = "Get inventory dashboard stats")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> stats = Map.of(
                "totalProducts", service.getTotalProducts(),
                "lowStockCount", service.getLowStockCount(),
                "outOfStockCount", service.getOutOfStockCount(),
                "totalInventoryValue", service.getTotalInventoryValue()
        );
        return ResponseEntity.ok(stats);
    }
}
