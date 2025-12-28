package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.ModelDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import com.alphawash.request.ModelRequest;
import com.alphawash.response.ModelResponse;
import com.alphawash.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_MODEL)
@RequiredArgsConstructor
@Tag(name = "Model", description = "API for managing vehicle models")
public class ModelController {

    private final ModelService modelService;

    @Operation(summary = "Tạo mới 1 dòng xe")
    @ApiResponse(responseCode = "200", description = "Tạo mới dòng xe thành công")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ModelResponse> create(@RequestBody ModelRequest request) {
        ModelDto dto = ModelConverter.INSTANCE.fromRequest(request);
        ModelDto saved = modelService.create(dto);
        return ResponseEntity.ok(ModelConverter.INSTANCE.toResponse(saved));
    }

    @Operation(summary = "Update a vehicle model")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model updated successfully"),
        @ApiResponse(responseCode = "404", description = "Model not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ModelResponse> update(@RequestBody ModelRequest request) {
        ModelDto dto = ModelConverter.INSTANCE.fromRequest(request);
        ModelDto updated = modelService.update( dto);
        return updated != null
                ? ResponseEntity.ok(ModelConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Xóa mềm một dòng xe")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Model deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Model not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER_CODE)
    public ResponseEntity<Boolean> delete(@PathVariable String code) {
        Boolean result = modelService.delete(code);
        return ResponseEntity.ok(result);
    }

    //Dành cho tạo mới hóa đơn
    @Operation(summary = "Lấy tất cả danh sách model xe và size")
    @GetMapping("by-brand")
    public ResponseEntity<List<ModelWithoutBrandDto>> getModel(@RequestParam String brandCode) {
        var result = modelService.findByBrandCode(brandCode);
        return ResponseEntity.ok(result);
    }
}
