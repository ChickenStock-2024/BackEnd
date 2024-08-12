package com.sascom.chickenstock.domain.member.error;

import com.sascom.chickenstock.domain.member.error.exception.MemberImageException;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.error.exception.MemberInfoChangeException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    protected ResponseEntity<?> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(MemberImageException.class)
    protected ResponseEntity<?> handleMemberImageException(MemberImageException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(MemberInfoChangeException.class)
    protected ResponseEntity<?> handleMemberPasswordChangeException(MemberInfoChangeException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
