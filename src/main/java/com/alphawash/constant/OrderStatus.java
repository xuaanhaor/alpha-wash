package com.alphawash.constant;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING"),
    DONE("DONE"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
