package com.sascom.chickenstock.domain.companylike.error.exception;

import com.sascom.chickenstock.domain.companylike.error.code.CompanyLikeErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class CompanyLikeNotFoundException extends ChickenStockException {
    private CompanyLikeNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static CompanyLikeNotFoundException of (CompanyLikeErrorCode errorCode) {
        return new CompanyLikeNotFoundException(errorCode);
    }
}
