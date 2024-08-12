package com.sascom.chickenstock.domain.member.error.exception;

import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;

public class MemberImageException extends ChickenStockException {
    private MemberImageException(ChickenStockErrorCode errorCode) {
        super(errorCode);
    }

    public static MemberImageException of (MemberErrorCode errorCode) {
        return new MemberImageException(errorCode);
    }
}
