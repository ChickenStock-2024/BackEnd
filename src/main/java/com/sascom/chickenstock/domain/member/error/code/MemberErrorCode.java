package com.sascom.chickenstock.domain.member.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 멤버입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    NO_FILE(HttpStatus.BAD_REQUEST,"003","이미지 파일이 존재하지 않습니다.");


    MemberErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "MEMBER"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}