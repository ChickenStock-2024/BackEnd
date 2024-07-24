package com.sascom.chickenstock.global.error.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ChickenStockErrorCode {

    ERROR(HttpStatus.BAD_REQUEST, "001", "알수없는 에러입니다. 담당자에게 문의해주세요."),
    RUNTIME_ERROR(HttpStatus.BAD_REQUEST, "002", "알수없는 에러입니다. 담당자에게 문의해주세요."),
    ILLEGAL_PATH(HttpStatus.BAD_REQUEST, "003", "잘못된 주소입니다."),
    ILLEGAL_PATH_ARGS(HttpStatus.BAD_REQUEST, "004", "잘못된 주소 ARGS 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "GLOBAL"+code;
        this.message = message;
    }
}
