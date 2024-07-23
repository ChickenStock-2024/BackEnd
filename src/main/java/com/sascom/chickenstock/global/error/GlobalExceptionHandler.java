package com.sascom.chickenstock.global.error;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.dto.ErrorResponse;
import com.sascom.chickenstock.global.error.exception.AuthException;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleChickenStockException (ChickenStockException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException (AuthException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ChickenStockErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
    }
}
