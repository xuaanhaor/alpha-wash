package com.alphawash.request;

import lombok.*;

@Builder
public record CustomerRequest
    ( String customerName,
     String phone,
     String note)
{}
