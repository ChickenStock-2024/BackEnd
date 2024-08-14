package com.sascom.chickenstock.domain.competition.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CompetitionErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 대회입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "003", "올바르지 않은 날짜입니다."),
    INVALID_DATE_TYPE(HttpStatus.BAD_REQUEST, "004", "잘못된 날짜 형식입니다."),
    INVALID_DURATION(HttpStatus.BAD_REQUEST, "005", "올바르지 않은 기간입니다."),
    CONFLICT(HttpStatus.BAD_REQUEST, "006", "기존 대회 기간과 중복됩니다."),
    INVALID_KEY(HttpStatus.UNAUTHORIZED, "007", "권한이 없습니다.");
    CompetitionErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "COMPETITION"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;

}
