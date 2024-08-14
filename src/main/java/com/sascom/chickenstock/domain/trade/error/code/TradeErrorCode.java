package com.sascom.chickenstock.domain.trade.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TradeErrorCode implements ChickenStockErrorCode {
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 상장 회사입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "002", "이미 처리되었거나 존재하지 않는 주문입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "003", "올바르지 않은 값입니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "004", "올바르지 않은 주문입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "005", "주문이 누락되었습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "006", "서버 로직 에러");

    TradeErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "ACCOUNT"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
