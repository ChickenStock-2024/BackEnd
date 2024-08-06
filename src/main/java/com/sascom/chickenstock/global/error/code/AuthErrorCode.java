package com.sascom.chickenstock.global.error.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ChickenStockErrorCode {

    AUTH_UNKNOWN(HttpStatus.BAD_REQUEST, "999", "알수없는 에러입니다. 담당자에게 문의해주세요."),
    NICKNAME_CONFLICT(HttpStatus.CONFLICT, "001", "중복된 닉네임입니다."),
    EMAIL_CONFLICT(HttpStatus.CONFLICT, "002", "중복된 이메일입니다."),
    SIGNUP_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "003", "회원가입 실패(비밀번호 미일치)"),
    SIGNUP_PASSWORD_CONDITION(HttpStatus.BAD_REQUEST, "004", "회원가입 실패(비밀번호 조건 미달성)"),
    SIGNUP_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "005", "회원가입 실패(올바르지 않은 요청)"),
    LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, "007", "로그인 실패"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "008", "Access Token 만료"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "009", "Refresh Token 만료"),
    OAUTH_REDIRECT_FAIL(HttpStatus.NOT_FOUND, "010", "oauth redirect 실패"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "011", "인증 토큰이 없습니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "012", "이메일 형식에 맞지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = "AUTH"+code;
        this.message = message;
    }
}
