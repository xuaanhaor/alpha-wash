package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.request.AutoLinkExecuteRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.AutoLinkExecuteResponse;
import com.alphawash.response.AutoLinkPreviewResponse;
import com.alphawash.service.AutoLinkVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_AUTO_LINK)
@RequiredArgsConstructor
@Tag(name = "Auto-Link Vehicles", description = "Admin batch-link unowned vehicles to customers derived from order history")
public class AutoLinkController {

    private final AutoLinkVehicleService autoLinkVehicleService;

    @Operation(
            summary = "Preview auto-link results",
            description =
                    "Returns all unlinked vehicles grouped into safe-to-link (1 customer) and conflict (multiple customers). No data is modified.")
    @GetMapping(Constant.PREVIEW_MERGE_ENDPOINT)
    public ResponseEntity<ApiResponse<AutoLinkPreviewResponse>> preview() {
        return ResponseEntity.ok(ApiResponse.success(autoLinkVehicleService.preview()));
    }

    @Operation(
            summary = "Execute auto-link",
            description =
                    "Links each safe vehicle to its single suggested customer. Pass dryRun=true to simulate without modifying data.")
    @PostMapping(Constant.EXECUTE_ENDPOINT)
    public ResponseEntity<ApiResponse<AutoLinkExecuteResponse>> execute(
            @RequestBody(required = false) AutoLinkExecuteRequest request) {
        AutoLinkExecuteRequest effectiveRequest = request != null ? request : new AutoLinkExecuteRequest(false);
        return ResponseEntity.ok(ApiResponse.success(autoLinkVehicleService.execute(effectiveRequest)));
    }
}
