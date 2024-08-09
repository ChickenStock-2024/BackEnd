package com.sascom.chickenstock.domain.history.error.exception;

import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.history.error.code.HistoryErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class HistoryNotFoundException extends ChickenStockException {
    private HistoryNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static HistoryNotFoundException of(HistoryErrorCode errorCode) {
        throw new HistoryNotFoundException(errorCode);
    }
}