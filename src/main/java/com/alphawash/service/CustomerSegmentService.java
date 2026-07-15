package com.alphawash.service;

import com.alphawash.dto.CustomerSegmentDto;
import com.alphawash.dto.CustomerSegmentDto.*;
import com.alphawash.request.CustomerSegmentRequest;
import java.util.List;
import java.util.UUID;

public interface CustomerSegmentService {

    List<CustomerSegmentDto> getAllSegments();

    CustomerSegmentDto createSegment(CustomerSegmentRequest request);

    CustomerSegmentDto updateSegment(Long id, CustomerSegmentRequest request);

    void deleteSegment(Long id);

    void recomputeAllSegments();

    void recomputeSegment(String segmentCode);

    SegmentPreview previewSegment(CustomerSegmentRequest request);

    SegmentDashboard getSegmentDashboard();

    List<CustomerWithSegments> getCustomersWithSegments();

    List<SegmentBadge> getSegmentsForCustomer(UUID customerId);
}
