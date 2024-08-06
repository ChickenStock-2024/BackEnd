package com.sascom.chickenstock.global.error.exception;

import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;

public class AccessTokenExpireException extends ChickenStockException{
    private AccessTokenExpireException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static AccessTokenExpireException of (AuthErrorCode errorCode) {
        return new AccessTokenExpireException(errorCode);
    }
}
