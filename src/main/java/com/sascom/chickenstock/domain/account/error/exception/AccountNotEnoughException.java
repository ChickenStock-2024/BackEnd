package com.sascom.chickenstock.domain.account.error.exception;

import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class AccountNotEnoughException extends ChickenStockException {
    private AccountNotEnoughException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static AccountNotEnoughException of (AccountErrorCode errorCode) {
        return new AccountNotEnoughException(errorCode);
    }
}
