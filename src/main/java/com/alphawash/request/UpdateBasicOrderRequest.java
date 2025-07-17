package com.alphawash.request;

import java.util.UUID;

public record UpdateBasicOrderRequest(UUID id, BasicOrderRequest request) {}
