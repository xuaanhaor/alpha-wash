package com.alphawash.exception;

import com.alphawash.constant.ErrorConst;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Builder
public class InvalidArgumentException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public InvalidArgumentException(HttpStatus statusCode, String message) {
        this.status = statusCode;
        this.message = message;
    }

    public InvalidArgumentException(String message) {
        this.status = HttpStatus.BAD_REQUEST;
        this.message = ErrorConst.E005.formatted(message);
    }
}
