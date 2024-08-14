package com.sascom.chickenstock.domain.competition.error.exception;

import com.sascom.chickenstock.domain.competition.error.code.CompetitionErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class CompetitionCreateException extends ChickenStockException {
    private CompetitionCreateException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static CompetitionCreateException of (CompetitionErrorCode errorCode) {
        return new CompetitionCreateException(errorCode);
    }
}
