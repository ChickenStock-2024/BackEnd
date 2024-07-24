package com.sascom.chickenstock.domain.company.error.exception;

import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class CompanyNotFoundException extends ChickenStockException {
    private CompanyNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static CompanyNotFoundException of (CompanyErrorCode errorCode) {
        return new CompanyNotFoundException(errorCode);
    }
}
