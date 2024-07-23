package com.sascom.chickenstock.global.error.exception;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;

@Getter
public class ChickenStockException extends RuntimeException{
    private final ChickenStockErrorCode errorCode;

    protected ChickenStockException(ChickenStockErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected static ChickenStockException of(ChickenStockErrorCode errorCode) {
        return new ChickenStockException(errorCode);
    }
}
