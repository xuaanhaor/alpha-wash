package com.alphawash.constant;

import lombok.Getter;

@Getter
public enum PromotionStatus {
    DRAFT("DRAFT"), //Nháp
    SCHEDULED("SCHEDULED"),  //Lên lịch
    ACTIVE("ACTIVE"),  //Hoạt động
    PAUSED("PAUSE"),  //Tạm dừng
    ENDED("ENDED"),  //Kết thúc
    CANCELLED("CANCELLED");  //Hủy

    private final String value;

    PromotionStatus(String value) {
        this.value = value;
    }
}
