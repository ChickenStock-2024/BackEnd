package com.sascom.chickenstock.domain.ranking.error;

import com.sascom.chickenstock.domain.ranking.error.exception.RankingException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RankingExceptionHandler extends BaseExceptionHandler {
    @ExceptionHandler(RankingException.class)
    protected ResponseEntity<?> handleRankingException(RankingException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
