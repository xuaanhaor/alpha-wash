package com.alphawash.constant;

import lombok.Getter;

@Getter
public enum Size {
    S("S"),
    M("M"),
    L("L");

    private final String value;

    Size(String value) {
        this.value = value;
    }
}
