package com.sascom.chickenstock.domain.dailystockprice.error.exception;

import com.sascom.chickenstock.domain.dailystockprice.error.code.DailyStockPriceErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class KisTodayStockPriceException extends ChickenStockException {
    private KisTodayStockPriceException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static KisTodayStockPriceException of (DailyStockPriceErrorCode errorCode) {
        return new KisTodayStockPriceException(errorCode);
    }

}

