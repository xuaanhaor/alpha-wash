package com.alphawash.request;

import java.util.UUID;
import org.springframework.lang.Nullable;

public record BasicCustomerRequest(@Nullable UUID id, @Nullable String name, @Nullable String phone) {}
