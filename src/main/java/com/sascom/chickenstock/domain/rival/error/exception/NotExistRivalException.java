package com.sascom.chickenstock.domain.rival.error.exception;

import com.sascom.chickenstock.domain.rival.error.code.RivalErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class NotExistRivalException extends ChickenStockException {
    private NotExistRivalException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static NotExistRivalException of (RivalErrorCode errorCode) {
        return new NotExistRivalException(errorCode);
    }

}
