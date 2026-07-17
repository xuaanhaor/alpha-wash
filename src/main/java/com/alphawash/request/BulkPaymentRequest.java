package com.alphawash.request;

import java.util.List;
import java.util.UUID;

public record BulkPaymentRequest(
        List<UUID> orderIds,
        String paymentStatus) {}
