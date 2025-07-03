package com.alphawash.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TestResponse {
    String code;
    String message;
}
