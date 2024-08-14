package com.sascom.chickenstock.domain.dailystockprice.error;

import com.sascom.chickenstock.domain.dailystockprice.error.exception.KisTodayStockPriceException;
import com.sascom.chickenstock.domain.dailystockprice.error.exception.KisTokenException;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DailyStockPriceExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(KisTokenException.class)
    protected ResponseEntity<?> handleKisTokenException(KisTokenException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(KisTodayStockPriceException.class)
    protected ResponseEntity<?> handleKisTodayStockPriceException(KisTodayStockPriceException e) {
        log.error(e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }

}
