package com.alphawash.endpoint;

import com.alphawash.entity.Production;
import com.alphawash.request.ProductionRequest;
import com.alphawash.service.ProductionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping("/all")
    public ResponseEntity<List<Production>> getAllProductions() {
        // This method should return all productions
        // For now, we will return an empty response
        var productions = productionService.getAllProductions();
        return ResponseEntity.ok(productions);
    }

    @PostMapping("/insert")
    public ResponseEntity<Production> insertProduction(@RequestBody ProductionRequest request) {
        // This method should insert a new production
        // For now, we will return the inserted production
        var insertedProduction = productionService.insertProduction(request);
        return ResponseEntity.ok(insertedProduction);
    }
}
