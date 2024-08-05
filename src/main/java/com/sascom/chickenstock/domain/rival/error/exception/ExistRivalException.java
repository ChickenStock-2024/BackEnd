package com.sascom.chickenstock.domain.rival.error.exception;

import com.sascom.chickenstock.domain.rival.error.code.RivalErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class ExistRivalException extends ChickenStockException {
    private ExistRivalException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static ExistRivalException of (RivalErrorCode errorCode) {
        return new ExistRivalException(errorCode);
    }

}
