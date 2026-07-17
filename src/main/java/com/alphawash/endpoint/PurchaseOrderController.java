package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.PurchaseOrderDto;
import com.alphawash.request.PurchaseOrderReceiveRequest;
import com.alphawash.request.PurchaseOrderRequest;
import com.alphawash.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_PURCHASE_ORDERS)
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Manage purchase orders from suppliers")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @Operation(summary = "Get all purchase orders")
    @GetMapping(ROOT)
    public ResponseEntity<List<PurchaseOrderDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get purchase order by code")
    @GetMapping(CODE_PATH_PARAMETER)
    public ResponseEntity<PurchaseOrderDto> getByCode(@PathVariable String code) {
        PurchaseOrderDto dto = service.getByCode(code);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a purchase order")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<PurchaseOrderDto> create(@RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Update a purchase order")
    @PatchMapping(UPDATE_ENDPOINT + "/code/{code}")
    public ResponseEntity<PurchaseOrderDto> update(@PathVariable String code, @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderDto updated = service.update(code, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Receive items for a purchase order")
    @PostMapping(RECEIVE_ENDPOINT + "/code/{code}")
    public ResponseEntity<PurchaseOrderDto> receive(@PathVariable String code, @RequestBody PurchaseOrderReceiveRequest request) {
        return ResponseEntity.ok(service.receive(code, request));
    }

    @Operation(summary = "Cancel a purchase order")
    @PostMapping(CANCEL_ENDPOINT + "/code/{code}")
    public ResponseEntity<Void> cancel(@PathVariable String code) {
        service.cancel(code);
        return ResponseEntity.noContent().build();
    }
}
