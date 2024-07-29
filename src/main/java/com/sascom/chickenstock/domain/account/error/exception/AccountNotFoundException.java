package com.sascom.chickenstock.domain.account.error.exception;

import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class AccountNotFoundException extends ChickenStockException {
    private AccountNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static AccountNotFoundException of (AccountErrorCode errorCode) {
        return new AccountNotFoundException(errorCode);
    }
}
