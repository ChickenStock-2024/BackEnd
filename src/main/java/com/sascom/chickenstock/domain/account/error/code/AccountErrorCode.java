package com.sascom.chickenstock.domain.account.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AccountErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 계좌입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "003", "계좌 잔고 부족으로 구매가 불가능합니다."),
    DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "004", "중복된 정보입니다.");


    AccountErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "ACCOUNT"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
