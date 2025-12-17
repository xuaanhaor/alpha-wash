package com.alphawash.constant;

public enum PromotionMethod {
    FLYER("FLYER"), //Tờ rơi
    LOYAL("LOYAL"),  //Thân thiết
    ONLINE("ONLINE"),  //Online
    SMS("SMS"),   //SMS
    ZALO("ZALO"),
    OTHER("OTHER");  //Khác

    private final String value;

    PromotionMethod(String value) {
        this.value = value;
    }
}
