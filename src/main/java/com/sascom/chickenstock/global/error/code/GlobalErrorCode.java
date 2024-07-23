package com.sascom.chickenstock.global.error.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ChickenStockErrorCode {

    GLOBAL_ERROR(HttpStatus.BAD_REQUEST, "001", "알수없는 에러입니다. 담당자에게 문의해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "GLOBAL"+code;
        this.message = message;
    }
}
