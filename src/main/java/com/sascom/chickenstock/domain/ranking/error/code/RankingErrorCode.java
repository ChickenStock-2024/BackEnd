package com.sascom.chickenstock.domain.ranking.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RankingErrorCode implements ChickenStockErrorCode {
    // offset error
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 페이지입니다");

    RankingErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "RANKING"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
