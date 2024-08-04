package com.sascom.chickenstock.domain.rival.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RivalErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 라이벌입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    SAME_MEMBER(HttpStatus.BAD_REQUEST, "003", "자기 자신과는 라이벌 관계를 맺을 수 없습니다."),
    EXIST_RELATIONSHIP(HttpStatus.BAD_REQUEST, "004", "이미 존재하는 라이벌 관계입니다."),
    NOT_EXIST_RELATIONSHIP(HttpStatus.BAD_REQUEST, "005", "존재하지 않는 라이벌 관계입니다.");

    RivalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "RIVAL"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
