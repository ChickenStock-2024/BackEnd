package com.sascom.chickenstock.domain.account.error.exception;

import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class AccountDuplicateException extends ChickenStockException {
    private AccountDuplicateException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static AccountDuplicateException of (AccountErrorCode errorCode) {
        return new AccountDuplicateException(errorCode);
    }
}
