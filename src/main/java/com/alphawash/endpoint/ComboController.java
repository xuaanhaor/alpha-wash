package com.alphawash.endpoint;

import com.alphawash.converter.BrandConverter;
import com.alphawash.response.BrandResponse;
import com.alphawash.response.ComboGetAllResponse;
import com.alphawash.service.BrandService;
import com.alphawash.service.ServiceComboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.alphawash.constant.Constant.*;

@RestController
@RequestMapping(API_SERVICE_COMBO)
@RequiredArgsConstructor
@Tag(name = "Combo service", description = "Combo service management")
public class ComboController {
    private final ServiceComboService serviceComboService;

    @GetMapping(ROOT)
    public ResponseEntity<List<ComboGetAllResponse>> getAll() {
        return ResponseEntity.ok(serviceComboService.getAll());
    }
}
