package com.eduplatform.common.exceptions;

import com.eduplatform.common.dtos.web.ReponseUtil;
import com.eduplatform.common.utils.ExceptionMessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ReponseUtil<Void>> handleValidation(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ReponseUtil.error("VALIDATION_ERROR", message, ExceptionMessageUtil.getDetail(serviceName, exception)));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<ReponseUtil<Void>> handleMalformedInput(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ReponseUtil.error("VALIDATION_ERROR", exception.getMessage(), ExceptionMessageUtil.getDetail(serviceName, exception)));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ReponseUtil<Void>> handleLogic(BaseException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ReponseUtil.error(exception.getCode(), exception.getMessage(), ExceptionMessageUtil.getDetail(serviceName, exception)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReponseUtil<Void>> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ReponseUtil.error("INTERNAL_ERROR", "An unexpected error occurred", ExceptionMessageUtil.getDetail(serviceName, exception)));
    }

}
