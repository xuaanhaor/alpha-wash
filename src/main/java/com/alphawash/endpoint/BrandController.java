package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.BrandConverter;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import com.alphawash.request.BrandRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.BrandResponse;
import com.alphawash.service.BrandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_BRANDS)
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping(ROOT)
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(BrandConverter.INSTANCE.toResponse(brandService.getAll())));
    }

    @GetMapping(API_BRAND_WITH_MODELS)
    public ResponseEntity<ApiResponse<List<BrandWithModelDto>>> getBrandsWithModels() {
        List<BrandWithModelDto> result = brandService.getBrandWithModel();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable Long id) {
        BrandDto dto = brandService.getById(id);
        return dto != null
                ? ResponseEntity.ok(ApiResponse.success(BrandConverter.INSTANCE.toResponse(dto)))
                : ResponseEntity.notFound().build();
    }

    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ApiResponse<BrandResponse>> create(@RequestBody BrandRequest request) {
        BrandDto dto = BrandConverter.INSTANCE.fromRequest(request);
        BrandDto saved = brandService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(BrandConverter.INSTANCE.toResponse(saved)));
    }

    @PutMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<BrandResponse>> update(@PathVariable Long id, @RequestBody BrandRequest request) {
        BrandDto dto = BrandConverter.INSTANCE.fromRequest(request);
        BrandDto updated = brandService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ApiResponse.success(BrandConverter.INSTANCE.toResponse(updated)))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
