package com.sascom.chickenstock.global.error.code;

import org.springframework.http.HttpStatus;

public interface ChickenStockErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
