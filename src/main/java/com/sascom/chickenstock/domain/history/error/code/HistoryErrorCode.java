package com.sascom.chickenstock.domain.history.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HistoryErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 기록입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "잘못된 취소 요청입니다.");

    HistoryErrorCode(HttpStatus status, String code, String message) {
        this.status= status;
        this.code = "HISTORY" + code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}