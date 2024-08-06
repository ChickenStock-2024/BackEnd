package com.sascom.chickenstock.domain.history.error;

import com.sascom.chickenstock.domain.account.error.exception.AccountNotEnoughException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.history.error.exception.HistoryNotFoundException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class HistoryExceptionHandler extends BaseExceptionHandler{
    @ExceptionHandler(AccountNotFoundException.class)
    protected ResponseEntity<?> handleHistoryNotFoundException(HistoryNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
