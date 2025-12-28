package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.BrandConverter;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.BrandWithModelDto;
import com.alphawash.request.BrandRequest;
import com.alphawash.response.BrandResponse;
import com.alphawash.service.BrandService;
import com.alphawash.util.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_BRANDS)
@RequiredArgsConstructor
@Tag(name = "Brand", description = "Brand management and vehicle models listing")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Lấy tất cả hãng xe active")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved brand list")})
    @GetMapping(ROOT)
    public ResponseEntity<List<BrandResponse>> getAll() {
        List<BrandDto> brands = new ArrayList<>(brandService.getAll());
        List<BrandResponse> result = new ArrayList<>(brands.size());
        for (BrandDto b : brands) {
            result.add(new BrandResponse(
                    b.getBrandId(),
                    b.getBrandCode(),
                    b.getBrandName()
            ));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lất tất cả hãng xe kèm theo model")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved brands with models")})
    @GetMapping(API_BRAND_WITH_MODELS)
    public ResponseEntity<List<BrandWithModelDto>> getBrandsWithModels() {
        List<BrandWithModelDto> result = brandService.getBrandWithModel();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy thông tin hãng xe theo BrandCode kèm theo model")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved brand with models"), @ApiResponse(responseCode = "404", description = "Brand not found")})
    @GetMapping(API_BRAND_WITH_MODELS + "/by-code")
    public BrandWithModelDto getBrandWithModelsByCode(@RequestParam String code) {
        BrandWithModelDto result = brandService.getBrandWithModelByBrandCode(code);
        return ObjectUtils.isNotNull(result) ? ResponseEntity.ok(result).getBody() : (BrandWithModelDto) ResponseEntity.notFound().build().getBody();
    }

    @Operation(summary = "Tạo mới hoặc cập nhật hãng xe")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Brand created successfully"), @ApiResponse(responseCode = "400", description = "Invalid input data")})
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<BrandResponse> create(@RequestBody BrandRequest request) {
        BrandDto dto = BrandConverter.INSTANCE.fromRequest(request);
        BrandDto saved = brandService.upsert(dto);
        return ResponseEntity.ok(BrandConverter.INSTANCE.toResponse(saved));
    }


    @Operation(summary = "Xóa logical hãng xe")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Brand deleted successfully"), @ApiResponse(responseCode = "404", description = "Brand not found")})
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER_CODE)
    public ResponseEntity<Boolean> delete(@PathVariable String code) {
        Boolean result = brandService.delete(code);
        return ResponseEntity.ok(result);
    }
}
