package com.alphawash.configuration;

import com.alphawash.exception.BusinessException;
import com.alphawash.exception.DuplicateVehicleException;
import com.alphawash.exception.InvalidArgumentException;
import com.alphawash.response.ApiResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý ngoại lệ khi phát hiện xe trùng biển số, trả kèm thông tin xe đã tồn tại.
     * @param ex Ngoại lệ xảy ra
     * @return ResponseEntity chứa ApiResponse với xe đã tồn tại trong data
     */
    @ExceptionHandler(DuplicateVehicleException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateVehicle(DuplicateVehicleException ex) {
        log.debug("Duplicate vehicle detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage(), ex.getExistingVehicle()));
    }

    /**
     * Xử lý lỗi Jackson khi không parse được request body (VD: LocalTime nhận empty string).
     * Giúp debug — trả về message cụ thể thay vì 400 mặc định của Spring.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.error("Request body parse error: {}", ex.getMessage());
        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error("Request body không hợp lệ: " + msg));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
    }
}
