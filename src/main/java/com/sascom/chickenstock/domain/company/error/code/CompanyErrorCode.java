package com.sascom.chickenstock.domain.company.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CompanyErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 회사입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다.");


    CompanyErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "COMPANY"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
