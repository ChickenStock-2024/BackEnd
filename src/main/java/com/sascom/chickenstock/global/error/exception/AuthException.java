package com.sascom.chickenstock.global.error.exception;

import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;

public class AuthException extends ChickenStockException{
    private AuthException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static AuthException of (AuthErrorCode errorCode) {
        return new AuthException(errorCode);
    }
}
