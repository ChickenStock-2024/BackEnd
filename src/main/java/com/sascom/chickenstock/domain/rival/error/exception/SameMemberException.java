package com.sascom.chickenstock.domain.rival.error.exception;

import com.sascom.chickenstock.domain.rival.error.code.RivalErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class SameMemberException extends ChickenStockException {
    private SameMemberException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static SameMemberException of (RivalErrorCode errorCode) {
        return new SameMemberException(errorCode);
    }

}
