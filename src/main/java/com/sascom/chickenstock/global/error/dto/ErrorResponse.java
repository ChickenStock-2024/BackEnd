package com.sascom.chickenstock.global.error.dto;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 공통 예외 반환 객체
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final String code;
    private final String message;

    public static ErrorResponse of(ChickenStockErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
