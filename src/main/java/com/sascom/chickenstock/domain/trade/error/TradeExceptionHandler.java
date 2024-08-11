package com.sascom.chickenstock.domain.trade.error;

import com.sascom.chickenstock.domain.trade.error.exception.TradeException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TradeExceptionHandler extends BaseExceptionHandler {
    @ExceptionHandler(TradeException.class)
    protected ResponseEntity<?> handleTradeNotFoundException(TradeException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
}
