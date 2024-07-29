package com.sascom.chickenstock.domain.trade.error.exception;

import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class TradeNotFoundException extends ChickenStockException {
    private TradeNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static TradeNotFoundException of (TradeErrorCode errorCode) {
        return new TradeNotFoundException(errorCode);
    }
}
