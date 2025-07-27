package com.alphawash.request;

import lombok.*;

public record CustomerRequest(String customerName, String phone, String note) {}
