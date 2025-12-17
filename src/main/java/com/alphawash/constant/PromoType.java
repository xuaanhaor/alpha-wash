package com.alphawash.constant;

import lombok.Getter;

@Getter
public enum PromoType {
    BILL_PERCENT("BILL_PERCENT"),      // Giảm % trên hóa đơn
    BILL_AMOUNT("BILL_AMOUNT"),       // Giảm số tiền trên hóa đơn
    SERVICE_PERCENT("SERVICE_PERCENT"),   // Giảm % theo dịch vụ
    SERVICE_AMOUNT("SERVICE_AMOUNT");     // Giảm số tiền theo dịch vụ

    private final String value;

    PromoType(String value) {
        this.value = value;
    }
}
