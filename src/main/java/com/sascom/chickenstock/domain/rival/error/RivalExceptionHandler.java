package com.sascom.chickenstock.domain.rival.error;

import com.sascom.chickenstock.domain.rival.error.exception.RivalNotFoundException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RivalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(RivalNotFoundException.class)
    protected ResponseEntity<?> handleRivalNotFoundException(RivalNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

}
