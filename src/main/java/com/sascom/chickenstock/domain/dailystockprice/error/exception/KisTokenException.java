package com.sascom.chickenstock.domain.dailystockprice.error.exception;

import com.sascom.chickenstock.domain.dailystockprice.error.code.DailyStockPriceErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class KisTokenException extends ChickenStockException {
    private KisTokenException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static KisTokenException of (DailyStockPriceErrorCode errorCode) {
        return new KisTokenException(errorCode);
    }

}
