package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_CUSTOMER_SEGMENTS;

import com.alphawash.dto.CustomerSegmentDto;
import com.alphawash.dto.CustomerSegmentDto.*;
import com.alphawash.request.CustomerSegmentRequest;
import com.alphawash.service.CustomerSegmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_CUSTOMER_SEGMENTS)
@RequiredArgsConstructor
@Tag(name = "Customer Segments", description = "Customer Segmentation API")
public class CustomerSegmentController {

    private final CustomerSegmentService segmentService;

    @GetMapping
    public ResponseEntity<List<CustomerSegmentDto>> getAllSegments() {
        return ResponseEntity.ok(segmentService.getAllSegments());
    }

    @PostMapping
    public ResponseEntity<CustomerSegmentDto> createSegment(@RequestBody CustomerSegmentRequest request) {
        return ResponseEntity.ok(segmentService.createSegment(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerSegmentDto> updateSegment(
            @PathVariable Long id, @RequestBody CustomerSegmentRequest request) {
        return ResponseEntity.ok(segmentService.updateSegment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSegment(@PathVariable Long id) {
        segmentService.deleteSegment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/preview")
    public ResponseEntity<SegmentPreview> previewSegment(@RequestBody CustomerSegmentRequest request) {
        return ResponseEntity.ok(segmentService.previewSegment(request));
    }

    @PostMapping("/recompute")
    public ResponseEntity<Void> recomputeAll() {
        segmentService.recomputeAllSegments();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recompute/{code}")
    public ResponseEntity<Void> recomputeSegment(@PathVariable String code) {
        segmentService.recomputeSegment(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<SegmentDashboard> getDashboard() {
        return ResponseEntity.ok(segmentService.getSegmentDashboard());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerWithSegments>> getCustomersWithSegments() {
        return ResponseEntity.ok(segmentService.getCustomersWithSegments());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SegmentBadge>> getCustomerSegments(@PathVariable UUID customerId) {
        return ResponseEntity.ok(segmentService.getSegmentsForCustomer(customerId));
    }
}
