package com.sascom.chickenstock.domain.member.error.code;

import com.sascom.chickenstock.global.error.code.ChickenStockErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ChickenStockErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 멤버입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "002", "올바르지 않은 값입니다."),
    NO_FILE(HttpStatus.BAD_REQUEST,"003","이미지 파일이 존재하지 않습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "004", "지원하는 파일이 아닙니다."),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "005", "파일 처리에 실패했습니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "006", "기존 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRMATION_ERROR(HttpStatus.BAD_REQUEST, "007", "변경하려는 비밀번호가 일치하지 않습니다."),
    UNAVAILABLE_NICKNAME(HttpStatus.BAD_REQUEST, "008", "사용할 수 없는 닉네임입니다.");

    MemberErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "MEMBER"+code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}