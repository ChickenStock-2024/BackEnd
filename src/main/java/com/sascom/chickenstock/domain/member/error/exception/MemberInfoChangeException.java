package com.sascom.chickenstock.domain.member.error.exception;

import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class MemberInfoChangeException extends ChickenStockException  {
    private MemberInfoChangeException(ChickenStockErrorCode errorCode) { super(errorCode); }

    public static MemberInfoChangeException of(MemberErrorCode errorCode) {
        return new MemberInfoChangeException(errorCode);
    }
}
