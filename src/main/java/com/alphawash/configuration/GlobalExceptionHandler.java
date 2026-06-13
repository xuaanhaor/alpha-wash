package com.alphawash.configuration;

import com.alphawash.exception.BusinessException;
import com.alphawash.exception.InvalidArgumentException;
import com.alphawash.response.ApiResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các ngoại lệ chung trong ứng dụng.
     * @param ex Ngoại lệ xảy ra
     * @return ResponseEntity chứa ApiResponse với thông tin lỗi
     */
    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ex.printStackTrace(); // debug, sau này log bằng logger
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý ngoại lệ khi không tìm thấy tài nguyên.
     * @param ex Ngoại lệ xảy ra
     * @return ResponseEntity chứa ApiResponse với thông tin lỗi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed: " + errorMsg));
    }

    /**
     * Xử lý ngoại lệ nghiệp vụ.
     * @param ex Ngoại lệ xảy ra
     * @return ResponseEntity chứa ApiResponse với thông tin lỗi
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(BusinessException ex) {
        log.debug("Business Exception occurred: {}", ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }
}
