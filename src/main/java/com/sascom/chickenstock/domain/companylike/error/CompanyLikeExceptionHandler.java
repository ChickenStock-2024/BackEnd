package com.sascom.chickenstock.domain.companylike.error;

import com.sascom.chickenstock.domain.companylike.error.exception.CompanyLikeNotFoundException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CompanyLikeExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(CompanyLikeNotFoundException.class)
    protected ResponseEntity<?> handleCompanyLikeNotFoundException(CompanyLikeNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
