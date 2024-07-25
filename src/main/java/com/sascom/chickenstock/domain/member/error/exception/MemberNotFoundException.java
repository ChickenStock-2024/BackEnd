package com.sascom.chickenstock.domain.member.error.exception;

import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class MemberNotFoundException extends ChickenStockException {
    private MemberNotFoundException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static MemberNotFoundException of (MemberErrorCode errorCode) {
        return new MemberNotFoundException(errorCode);
    }
}
