package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.ModelConverter;
import com.alphawash.dto.ModelDto;
import com.alphawash.request.ModelRequest;
import com.alphawash.response.ModelResponse;
import com.alphawash.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(API_MODEL)
@RequiredArgsConstructor
@Tag(name = "Model", description = "API for managing vehicle models")
public class ModelController {

    private final ModelService modelService;

    @Operation(summary = "Get all vehicle models")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of models")
    @GetMapping(ROOT)
    public ResponseEntity<List<ModelResponse>> getAll() {
        return ResponseEntity.ok(ModelConverter.INSTANCE.toResponse(modelService.getAll()));
    }

    @Operation(summary = "Get model by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model found"),
            @ApiResponse(responseCode = "404", description = "Model not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ModelResponse> getById(@PathVariable Long id) {
        ModelDto dto = modelService.getById(id);
        return dto != null
                ? ResponseEntity.ok(ModelConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new vehicle model")
    @ApiResponse(responseCode = "200", description = "Model created successfully")
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
    public ResponseEntity<ModelResponse> update(@PathVariable Long id, @RequestBody ModelRequest request) {
        ModelDto dto = ModelConverter.INSTANCE.fromRequest(request);
        ModelDto updated = modelService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ModelConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a vehicle model by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Model deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Model not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
