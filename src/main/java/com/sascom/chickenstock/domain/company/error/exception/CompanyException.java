package com.sascom.chickenstock.domain.company.error.exception;

import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class CompanyException extends ChickenStockException {
    private CompanyException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static CompanyException of (CompanyErrorCode errorCode) {
        return new CompanyException(errorCode);
    }
}
