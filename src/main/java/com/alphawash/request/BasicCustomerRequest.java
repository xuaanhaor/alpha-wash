package com.alphawash.request;

import java.util.UUID;

public record BasicCustomerRequest(UUID id, String name, String phone) {}
