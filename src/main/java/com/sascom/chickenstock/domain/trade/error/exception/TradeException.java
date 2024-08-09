package com.sascom.chickenstock.domain.trade.error.exception;

import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class TradeException extends ChickenStockException {
    private TradeException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static TradeException of (TradeErrorCode errorCode) {
        return new TradeException(errorCode);
    }
}
