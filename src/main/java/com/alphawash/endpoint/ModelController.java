package com.alphawash.controller;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.ModelDto;
import com.alphawash.request.ModelRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.ModelResponse;
import com.alphawash.service.ModelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_MODEL)
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping(ROOT)
    public ResponseEntity<ApiResponse<List<ModelResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(ModelConverter.INSTANCE.toResponse(modelService.getAll())));
    }

    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<ModelResponse>> getById(@PathVariable Long id) {
        ModelDto dto = modelService.getById(id);
        return dto != null
                ? ResponseEntity.ok(ApiResponse.success(ModelConverter.INSTANCE.toResponse(dto)))
                : ResponseEntity.notFound().build();
    }

    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ApiResponse<ModelResponse>> create(@RequestBody ModelRequest request) {
        ModelDto dto = ModelConverter.INSTANCE.fromRequest(request);
        ModelDto saved = modelService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(ModelConverter.INSTANCE.toResponse(saved)));
    }

    @PutMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<ModelResponse>> update(@PathVariable Long id, @RequestBody ModelRequest request) {
        ModelDto dto = ModelConverter.INSTANCE.fromRequest(request);
        ModelDto updated = modelService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ApiResponse.success(ModelConverter.INSTANCE.toResponse(updated)))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
