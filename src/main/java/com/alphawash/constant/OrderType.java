package com.alphawash.constant;

import lombok.Getter;

@Getter
public enum OrderType {

    SERVICE("SERVICE"),          // Order Detail làm dịch vụ
    COMBO("COMBO"),              // Order Detail bán combo
    USE_COMBO("USE_COMBO"),      // Order Detail dùng combo đã mua
    EXTRA("EXTRA");              // Phát sinh ngoài

    private final String value;

    OrderType(String value) {
        this.value = value;
    }
}