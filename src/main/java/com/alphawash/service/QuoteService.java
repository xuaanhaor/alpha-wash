package com.alphawash.service;

import com.alphawash.constant.QuoteStatus;
import com.alphawash.request.QuoteRequest;
import com.alphawash.request.QuoteStatusRequest;
import com.alphawash.response.PageResponse;
import com.alphawash.response.QuoteResponse;
import java.util.UUID;

public interface QuoteService {

    PageResponse<QuoteResponse> list(String search, QuoteStatus status, int page, int size);

    QuoteResponse getById(UUID id);

    QuoteResponse create(QuoteRequest request);

    QuoteResponse update(UUID id, QuoteRequest request);

    void delete(UUID id);

    QuoteResponse updateStatus(UUID id, QuoteStatusRequest request);
}
