package com.econocom.authentication.shared.exception;

import com.econocom.authentication.domain.exception.BusinessException;
import com.econocom.authentication.shared.error.ErrorCode;
import com.econocom.authentication.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        log.warn("{} - {}", errorCode.getCode(), exception.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .status(errorCode.getHttpStatus())
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .status(400)
                .message(message)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception) {

        log.error("Unexpected error", exception);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .status(500)
                .message("An unexpected error occurred.")
                .build();

        return ResponseEntity.internalServerError()
                .body(response);
    }

}
