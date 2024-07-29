package com.sascom.chickenstock.domain.competition.error;

import com.sascom.chickenstock.domain.competition.error.exception.CompetitionNotFoundException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CompetitionExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(CompetitionNotFoundException.class)
    protected ResponseEntity<?> handleCompetitionNotFoundException(CompetitionNotFoundException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
