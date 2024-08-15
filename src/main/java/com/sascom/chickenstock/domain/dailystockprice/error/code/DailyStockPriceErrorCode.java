package com.sascom.chickenstock.domain.dailystockprice.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DailyStockPriceErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 라이벌입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    CANNOT_GET_KIS_TOKEN(HttpStatus.BAD_REQUEST, "003", "KIS API 호출을 통해 ACCESS_TOKEN 획득 실패했습니다."),
    CANNOT_GET_TODAY_STOCK_PRICE(HttpStatus.BAD_REQUEST, "004", "KIS_API 호출을 통해 당일 주식 정보를 가져오는데 실패했습니다.");

    DailyStockPriceErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "STOCKPRICE"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
