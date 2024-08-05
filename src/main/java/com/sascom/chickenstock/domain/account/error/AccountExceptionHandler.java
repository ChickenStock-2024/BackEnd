package com.sascom.chickenstock.domain.account.error;

import com.sascom.chickenstock.domain.account.error.exception.AccountDuplicateException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotEnoughException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    protected ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(AccountNotEnoughException.class)
    protected ResponseEntity<?> handleAccountNotEnoughException(AccountNotEnoughException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(AccountDuplicateException.class)
    protected ResponseEntity<?> handleAccountDuplicateException(AccountDuplicateException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
