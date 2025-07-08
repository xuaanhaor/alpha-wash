package com.alphawash.endpoint;

import com.alphawash.service.VehicleCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-catalog")
@RequiredArgsConstructor
public class VehicleCatalogController {

    private final VehicleCatalogService vehicleCatalogService;

    @GetMapping("/allBrand")
    public ResponseEntity<List<String>> getAllBrandVehicleCatalog() {
        var listBrand = vehicleCatalogService.getAllBrands();
        return ResponseEntity.ok(listBrand);
    }

}