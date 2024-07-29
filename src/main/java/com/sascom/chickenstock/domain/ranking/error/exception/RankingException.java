package com.sascom.chickenstock.domain.ranking.error.exception;

import com.sascom.chickenstock.domain.ranking.error.code.RankingErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class RankingException extends ChickenStockException {
    private RankingException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static RankingException of(RankingErrorCode errorCode) {
        return new RankingException(errorCode);
    }
}
