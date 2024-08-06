package com.sascom.chickenstock.global.error;

import com.sascom.chickenstock.global.error.code.GlobalErrorCode;
import com.sascom.chickenstock.global.error.dto.ErrorResponse;
import com.sascom.chickenstock.global.error.exception.AuthException;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.metamodel.mapping.ordering.ast.PathResolutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleChickenStockException (ChickenStockException e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return createErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException (Exception e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        return createErrorResponse(GlobalErrorCode.ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException (RuntimeException e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        return createErrorResponse(GlobalErrorCode.RUNTIME_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException (NoResourceFoundException e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        return createErrorResponse(GlobalErrorCode.ILLEGAL_PATH);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException (MethodArgumentTypeMismatchException e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return createErrorResponse(GlobalErrorCode.ILLEGAL_PATH_ARGS);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException (AuthException e) {
        log.error("class: {}, message: {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return createErrorResponse(e.getErrorCode());
    }
}
