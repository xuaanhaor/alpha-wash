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

    @Operation(summary = "Get all vehicle brands")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved brand list")})
    @GetMapping(SEARCH_ENDPOINT)
    public ResponseEntity<List<BrandResponse>> getAll() {
        return ResponseEntity.ok(BrandConverter.INSTANCE.toResponse(brandService.getAll()));
    }

    @Operation(summary = "Get all brands with their associated models")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved brands with models")})
    @GetMapping(API_BRAND_WITH_MODELS)
    public ResponseEntity<List<BrandWithModelDto>> getBrandsWithModels() {
        List<BrandWithModelDto> result = brandService.getBrandWithModel();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get a brand with its associated models by brand ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved brand with models"),
                @ApiResponse(responseCode = "404", description = "Brand not found")
            })
    @GetMapping(API_BRAND_WITH_MODELS + ID_PATH_PARAMETER)
    public BrandWithModelDto getBrandWithModelsById(@PathVariable Long id) {
        BrandWithModelDto result = brandService.getBrandWithModelById(id);
        return ObjectUtils.isNotNull(result)
                ? ResponseEntity.ok(result).getBody()
                : (BrandWithModelDto) ResponseEntity.notFound().build().getBody();
    }

    @Operation(summary = "Get a brand by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Brand found"),
                @ApiResponse(responseCode = "404", description = "Brand not found with given ID")
            })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<BrandResponse> getById(@PathVariable Long id) {
        BrandDto dto = brandService.getById(id);
        return dto != null
                ? ResponseEntity.ok(BrandConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new brand")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Brand created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<BrandResponse> create(@RequestBody BrandRequest request) {
        BrandDto dto = BrandConverter.INSTANCE.fromRequest(request);
        BrandDto saved = brandService.create(dto);
        return ResponseEntity.ok(BrandConverter.INSTANCE.toResponse(saved));
    }

    @Operation(summary = "Update a brand by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
                @ApiResponse(responseCode = "404", description = "Brand not found")
            })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<BrandResponse> update(@PathVariable Long id, @RequestBody BrandRequest request) {
        BrandDto dto = BrandConverter.INSTANCE.fromRequest(request);
        BrandDto updated = brandService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(BrandConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a brand by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Brand not found")
            })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
