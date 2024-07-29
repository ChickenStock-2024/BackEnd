package com.sascom.chickenstock.domain.competition.error.exception;

import com.sascom.chickenstock.domain.competition.error.code.CompetitionErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class CompetitionNotFoundException extends ChickenStockException {
    private CompetitionNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static CompetitionNotFoundException of (CompetitionErrorCode errorCode) {
        return new CompetitionNotFoundException(errorCode);
    }
}
