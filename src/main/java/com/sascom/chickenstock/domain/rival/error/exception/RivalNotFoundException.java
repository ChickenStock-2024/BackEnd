package com.sascom.chickenstock.domain.rival.error.exception;

import com.sascom.chickenstock.domain.rival.error.code.RivalErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class RivalNotFoundException extends ChickenStockException {
    private RivalNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static RivalNotFoundException of (RivalErrorCode errorCode) {
        return new RivalNotFoundException(errorCode);
    }

}
