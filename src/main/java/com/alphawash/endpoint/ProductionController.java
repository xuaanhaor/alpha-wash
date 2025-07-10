package com.alphawash.endpoint;

import com.alphawash.request.ProductionRequest;
import com.alphawash.response.ProductionResponse;
import com.alphawash.entity.Production;
import com.alphawash.service.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductionResponse>> getAllProductions() {
        List<ProductionResponse> productions = productionService.getAllProductions();
        return ResponseEntity.ok(productions);
    }

    @PostMapping("/insert")
    public ResponseEntity<ProductionResponse> insertProduction(@RequestBody ProductionRequest request) {
        ProductionResponse insertedProduction = productionService.insertProduction(request);
        return ResponseEntity.ok(insertedProduction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionResponse> getProductionById(@PathVariable Long id) {
        ProductionResponse production = productionService.getProductionById(id);
        return ResponseEntity.ok(production);
    }

}
