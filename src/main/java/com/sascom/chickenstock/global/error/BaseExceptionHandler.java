package com.sascom.chickenstock.global.error;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseExceptionHandler {
    protected ResponseEntity<ErrorResponse> createErrorResponse(ChickenStockErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
    }
}
