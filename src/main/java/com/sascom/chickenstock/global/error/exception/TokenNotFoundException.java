package com.sascom.chickenstock.global.error.exception;

import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;

public class TokenNotFoundException extends ChickenStockException{
    private TokenNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static TokenNotFoundException of (AuthErrorCode errorCode) {
        return new TokenNotFoundException(errorCode);
    }
}
