package com.alphawash.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Builder
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public BusinessException(HttpStatus statusCode, String message) {
        this.status = statusCode;
        this.message = message;
    }
}
